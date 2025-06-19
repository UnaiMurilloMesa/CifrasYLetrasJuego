package org.umm.cifrasyletras.application.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.infrastructure.socket.GameSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    private final GameSocketHandler gameSocketHandler;

    public RoomService(@Lazy GameSocketHandler gameSocketHandler) {
        this.gameSocketHandler = gameSocketHandler;
    }

    public Room createRoom(User creator, boolean isPrivate) throws IOException {
        String roomId = UUID.randomUUID().toString().substring(0, 6);
        Room room = new Room(roomId, creator, isPrivate);
        rooms.put(roomId, room);
        gameSocketHandler.sendRoomUpdate(roomId);
        return room;
    }

    public Room joinRoom(String roomId, User user) throws IOException {
        Room room = rooms.get(roomId);
        if (room != null && !room.isFull()) {
            room.addPlayer(user);
            gameSocketHandler.sendRoomUpdate(roomId);
            return room;
        }
        return null;
    }

    public void leaveRoom(String roomId, User user) throws IOException {
        Room room = rooms.get(roomId);
        if (room != null) {
            room.removePlayer(user);
            if (room.getPlayers().isEmpty()) {
                rooms.remove(roomId);
            } else if (room.getOwner().equals(user)) {
                room.setOwner(room.getPlayers().get(0));
            }
            gameSocketHandler.sendRoomUpdate(roomId);
        }
    }

    public Room getRoom(String roomId) {
        return rooms.get(roomId);
    }
}
