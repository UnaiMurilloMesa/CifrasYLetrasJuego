package org.umm.cifrasyletras.infrastructure.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.umm.cifrasyletras.application.services.RoomService;
import org.umm.cifrasyletras.domain.model.Room;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameSocketHandler extends TextWebSocketHandler {
    private final RoomService roomService;
    private final ObjectMapper mapper = new ObjectMapper();

    private final Map<String, Set<WebSocketSession>> sessionsByRoom = new ConcurrentHashMap<>();

    private final Map<String, String> roomBySession = new ConcurrentHashMap<>();

    public GameSocketHandler(RoomService roomService) {
        this.roomService = roomService;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = roomBySession.remove(session.getId());
        if (roomId != null) {
            sessionsByRoom.getOrDefault(roomId, Collections.emptySet()).remove(session);
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        if ("init".equals(type)) {
            String roomId = (String) payload.get("roomId");
            String username = (String) payload.get("user");

            if (roomId != null && username != null) {
                roomBySession.put(session.getId(), roomId);
                sessionsByRoom.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
                sendRoomUpdate(roomId);
            }
        }
    }

    public void sendRoomUpdate(String roomId) throws IOException {
        Room room = roomService.getRoom(roomId);
        if (room == null) {return;}

        Set<WebSocketSession> sessions = sessionsByRoom.getOrDefault(roomId, Collections.emptySet());
        if (sessions.isEmpty()) {return;}

        String roomJson = mapper.writeValueAsString(room);

        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(roomJson));
            }
        }
    }
}
