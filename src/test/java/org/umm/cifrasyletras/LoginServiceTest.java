package org.umm.cifrasyletras;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.umm.cifrasyletras.application.services.LoginService;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.domain.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.anyString;

class LoginServiceTest {

    private UserRepository userRepository;
    private LoginService loginService;

    @BeforeEach
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        loginService = new LoginService(userRepository);
    }

    @Test
    public void loginCreateUserWithUniqueDisplayName() {
        String name = "User";
        when(userRepository.findByDisplayName(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User user = loginService.login(name);

        assertNotNull(user);
        assertTrue(user.getDisplayName().startsWith("User#"));
        assertNotNull(user.getId());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void loginCreatesUserWithRepeatedDisplayName() {
        String name = "User";

        when(userRepository.findByDisplayName(startsWith("User#"))).thenReturn(Optional.of(new User()), Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        User user = loginService.login(name);

        assertNotNull(user);
        assertTrue(user.getDisplayName().startsWith("User#"));
        verify(userRepository, times(2)).findByDisplayName(anyString());
    }
}
