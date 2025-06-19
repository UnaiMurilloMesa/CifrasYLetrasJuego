package org.umm.cifrasyletras;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.umm.cifrasyletras.application.services.RoomService;
import org.umm.cifrasyletras.domain.model.Room;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.interfaces.rest.RoomController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.Mockito.*;

@WebMvcTest(RoomController.class)
public class RoomControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RoomService roomService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateRoom() throws Exception {
        User user = new User("1", "Unai");
        Room room = new Room("123456", user, false);

        when(roomService.createRoom(any(), anyBoolean())).thenReturn(room);

        mockMvc.perform(post("/api/room/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());

        verify(roomService, times(1)).createRoom(any(), eq(true));
    }
}
