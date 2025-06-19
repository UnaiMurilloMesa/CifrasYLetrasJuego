package org.umm.cifrasyletras;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.umm.cifrasyletras.application.services.LoginService;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.interfaces.rest.LoginController;

import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoginController.class)
public class LoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LoginService loginService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void loginEndpointGetsUser() throws Exception {
        User fakeUser = new User("1", "Pepito#1234");
        when(loginService.login("Pepito")).thenReturn(fakeUser);

        String body = objectMapper.writeValueAsString(Map.of("name", "Pepito"));

        var result = mockMvc.perform(post("/api/login")
                    .contentType("application/json")
                    .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.displayName").value("Pepito#1234"));

        System.out.println(result.andReturn().getResponse().getContentAsString());
    }
}
