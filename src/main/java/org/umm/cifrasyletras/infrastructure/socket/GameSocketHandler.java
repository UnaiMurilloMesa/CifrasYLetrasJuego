package org.umm.cifrasyletras.infrastructure.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.umm.cifrasyletras.application.services.RoomService;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.infrastructure.persistence.WebSocketSessionManager;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameSocketHandler extends TextWebSocketHandler {

    private final RoomService roomService;
    private final ObjectMapper mapper = new ObjectMapper();
    private final WebSocketSessionManager sessionManager;

    public GameSocketHandler(RoomService roomService, WebSocketSessionManager sessionManager) {
        this.roomService = roomService;
        this.sessionManager = sessionManager;
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessionManager.removeSession(session.getId());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        switch (type) {
            case "init" -> handleInit(session, payload);
            case "startGame" -> handleStartGame(payload);
        }
    }

    private void handleInit(WebSocketSession session, Map<String, Object> payload) throws IOException {
        String roomId = (String) payload.get("roomId");
        String username = (String) payload.get("user");

        if (roomId != null && username != null) {
            sessionManager.registerSession(session.getId(), roomId, session);
            sendRoomUpdate(roomId);
        }
    }

    private void handleStartGame(Map<String, Object> payload) throws IOException {
        String roomId = (String) payload.get("roomId");
        String userId = (String) payload.get("userId");

        Room room = roomService.getRoom(roomId);
        if (roomId != null && room.getOwner().getId().equals(userId) && room.getPlayers().size() >= 2 && !room.isGameStarted()) {
            roomService.startGame(roomId);

            Map<String, Object> gameStartedMessage = Map.of("type", "gameStarted", "roomId", roomId);

            String json = mapper.writeValueAsString(gameStartedMessage);
            broadcastToRoom(roomId, json);
        }
    }

    public void sendRoomUpdate(String roomId) throws IOException {
        Room room = roomService.getRoom(roomId);
        if (room == null) {return;}

        String roomJson = mapper.writeValueAsString(room);
        broadcastToRoom(roomId, roomJson);
    }

    private void broadcastToRoom(String roomId, String json) throws IOException {
        TextMessage message = new TextMessage(json);
        for (WebSocketSession session : sessionManager.getSessions(roomId)) {
            if (session.isOpen()) {
                session.sendMessage(message);
            }
        }
    }
}
