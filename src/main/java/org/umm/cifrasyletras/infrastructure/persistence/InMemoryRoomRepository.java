package org.umm.cifrasyletras.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Repository;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.repository.RoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryRoomRepository implements RoomRepository {
    private final Map<String, Room> rooms = new ConcurrentHashMap<>();

    @Override
    public Room save(Room room) {
        rooms.put(room.getId(), room);
        return room;
    }

    @Override
    public void delete(Room room) {
        rooms.remove(room.getId());
    }

    @Override
    public Room findById(String roomId) {
        return rooms.get(roomId);
    }

    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
}
