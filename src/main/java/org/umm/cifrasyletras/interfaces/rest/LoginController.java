package org.umm.cifrasyletras.interfaces.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.umm.cifrasyletras.application.services.LoginService;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.interfaces.rest.dto.LoginRequest;

@RestController
@RequestMapping("/api")
public class LoginController {
    private final LoginService loginService;

    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping("/login")
    public User login(@RequestBody LoginRequest loginRequest) {
        return loginService.login(loginRequest.getName());
    }
}
