package org.umm.cifrasyletras.interfaces.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.umm.cifrasyletras.application.services.RoomService;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.model.User;

import java.io.IOException;

@RestController
@RequestMapping("/api/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createRoom(@RequestBody User user) throws IOException {
        try {
            Room room = roomService.createRoom(user, true);
            return ResponseEntity.ok(room);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal Error creating room");
        }
    }

    @PostMapping("/join/{roomId}")
    public ResponseEntity<Room> joinRoom(@PathVariable String roomId, @RequestBody User user) throws IOException {
        Room room = roomService.joinRoom(roomId, user);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/leave/{roomId}")
    public void leaveRoom(@PathVariable String roomId, @RequestBody User user) throws IOException {
        roomService.leaveRoom(roomId, user);
    }

    @GetMapping("/{roomId}")
    public Room getRoom(@PathVariable String roomId) {
        return roomService.getRoom(roomId);
    }
}
