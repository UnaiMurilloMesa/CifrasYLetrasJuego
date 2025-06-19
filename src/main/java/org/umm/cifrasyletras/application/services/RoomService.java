package org.umm.cifrasyletras.application.services;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.domain.repository.RoomRepository;
import org.umm.cifrasyletras.infrastructure.socket.GameSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final GameSocketHandler gameSocketHandler;

    public RoomService(@Lazy GameSocketHandler gameSocketHandler, RoomRepository roomRepository) {
        this.gameSocketHandler = gameSocketHandler;
        this.roomRepository = roomRepository;
    }

    public Room createRoom(User creator, boolean isPrivate) throws IOException {
        String roomId = UUID.randomUUID().toString().substring(0, 6);
        Room room = new Room(roomId, creator, isPrivate);
        roomRepository.save(room);
        gameSocketHandler.sendRoomUpdate(roomId);
        System.out.println(creator.getDisplayName() + " created room: " + roomId);
        return room;
    }

    public Room joinRoom(String roomId, User user) throws IOException {
        Room room = roomRepository.findById(roomId);
        if (room != null && !room.isFull()) {
            room.addPlayer(user);
            gameSocketHandler.sendRoomUpdate(roomId);
            System.out.println(user.getDisplayName() + " joined room: " + roomId);
            return room;
        }
        System.out.println(user.getDisplayName() + " could not join room: " + roomId);
        return null;
    }

    public void leaveRoom(String roomId, User user) throws IOException {
        Room room = roomRepository.findById(roomId);
        if (room != null) {
            room.removePlayer(user);
            if (room.getPlayers().isEmpty()) {
                roomRepository.delete(room);
            } else if (room.getOwner().equals(user)) {
                room.setOwner(room.getPlayers().get(0));
            }
            gameSocketHandler.sendRoomUpdate(roomId);
        }
        System.out.println(user.getDisplayName() + " left room: " + roomId);
    }

    public Room getRoom(String roomId) {
        return roomRepository.findById(roomId);
    }

    public boolean isGameStarted(String roomId) {
        Room room = roomRepository.findById(roomId);
        return room.isGameStarted();
    }

    public void startGame(String roomId) throws IOException {
        Room room = roomRepository.findById(roomId);
        room.setGameStarted(true);
    }
}
