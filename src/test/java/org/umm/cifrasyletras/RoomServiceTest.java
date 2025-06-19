package org.umm.cifrasyletras;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.umm.cifrasyletras.application.services.RoomService;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.infrastructure.socket.GameSocketHandler;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {
    @Mock
    private GameSocketHandler gameSocketHandler;

    private RoomService roomService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        roomService = new RoomService(gameSocketHandler);
    }

    @Test
    public void testCreateRoom() throws IOException {
        User user = new User("123", "Unai");
        Room room = roomService.createRoom(user, false);

        assertNotNull(room);
        assertEquals(user, room.getOwner());
        assertTrue(room.getPlayers().contains(user));
        verify(gameSocketHandler, times(1)).sendRoomUpdate(room.getId());
    }

    @Test
    public void testJoinRoom() throws IOException {
        User owner = new User("1", "Owner");
        User user = new User("2", "Jugador");
        Room room = roomService.createRoom(owner, false);

        Room joinedRoom = roomService.joinRoom(room.getId(), user);
        assertNotNull(joinedRoom);
        assertTrue(joinedRoom.getPlayers().contains(user));
        verify(gameSocketHandler, times(2)).sendRoomUpdate(room.getId());
    }

    @Test
    public void testJoinRoomFull() throws IOException {
        User owner = new User("1", "Owner");
        User user1 = new User("2", "Jugador1");
        User user2 = new User("3", "Jugador2");

        Room room = roomService.createRoom(owner, false);
        room.addPlayer(user1);

        Room joinedRoom = roomService.joinRoom(room.getId(), user2);
        assertNull(joinedRoom);
    }
}
