package org.umm.cifrasyletras.domain.repository;

import org.umm.cifrasyletras.domain.model.Room;

import java.util.List;

public interface RoomRepository {
    Room save(Room room);
    void delete(Room room);
    Room findById(String roomId);
    List<Room> findAll();
}
