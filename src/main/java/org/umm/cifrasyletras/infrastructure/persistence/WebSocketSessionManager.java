package org.umm.cifrasyletras.infrastructure.persistence;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketSessionManager {
    private final Map<String, Set<WebSocketSession>> sessionByRoom = new ConcurrentHashMap<>();
    private final Map<String, String> roomBySession = new ConcurrentHashMap<>();

    public void registerSession(String sessionId, String roomId, WebSocketSession session) {
        roomBySession.put(roomId, sessionId);
        sessionByRoom.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
    }

    public void removeSession(String sessionId) {
        String roomId = roomBySession.remove(sessionId);
        if (roomId != null) {
            Set<WebSocketSession> sessions = sessionByRoom.get(roomId);
            if (sessions != null) {
                sessions.removeIf(s -> s.getId().equals(sessionId));
                if (sessions.isEmpty()) {
                    sessionByRoom.remove(roomId);
                }
            }
        }
    }

    public Set<WebSocketSession> getSessions(String roomId) {
        return sessionByRoom.getOrDefault(roomId, Collections.emptySet());
    }

    public String getRoomId(String sessionId) {
        return roomBySession.get(sessionId);
    }
}
