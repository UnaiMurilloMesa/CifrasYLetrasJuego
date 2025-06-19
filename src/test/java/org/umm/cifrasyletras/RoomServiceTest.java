package org.umm.cifrasyletras;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.umm.cifrasyletras.application.services.RoomService;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.domain.repository.RoomRepository;
import org.umm.cifrasyletras.infrastructure.socket.GameSocketHandler;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoomServiceTest {
    @Mock
    private GameSocketHandler gameSocketHandler;

    @Mock
    private RoomRepository roomRepository;

    private RoomService roomService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        roomService = new RoomService(gameSocketHandler, roomRepository);
    }

    @Test
    public void testCreateRoom() throws IOException {
        User user = new User("123", "Unai");
        when(roomRepository.save(any(Room.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Room room = roomService.createRoom(user, false);

        assertNotNull(room);
        assertEquals(user, room.getOwner());
        assertTrue(room.getPlayers().contains(user));

        verify(roomRepository).save(room);
        verify(gameSocketHandler).sendRoomUpdate(room.getId());
    }

    @Test
    public void testJoinRoom() throws IOException {
        User owner = new User("1", "Owner");
        User user = new User("2", "Jugador");

        Room room = new Room("abc123", owner, false);
        when(roomRepository.findById("abc123")).thenReturn(room);
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room joinedRoom = roomService.joinRoom("abc123", user);

        assertNotNull(joinedRoom);
        assertTrue(joinedRoom.getPlayers().contains(user));

        verify(roomRepository).save(room);
        verify(gameSocketHandler).sendRoomUpdate("abc123");
    }

    @Test
    public void testJoinRoomFull() throws IOException {
        User owner = new User("1", "Owner");
        User user1 = new User("2", "Jugador1");
        User user2 = new User("3", "Jugador2");

        Room room = new Room("xyz789", owner, false);
        room.addPlayer(user1);

        when(roomRepository.findById("xyz789")).thenReturn(room);

        Room joinedRoom = roomService.joinRoom("xyz789", user2);

        assertNull(joinedRoom);
        verify(roomRepository, never()).save(any());
        verify(gameSocketHandler, never()).sendRoomUpdate(any());
    }
}
