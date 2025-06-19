package org.umm.cifrasyletras.application.services;

import org.springframework.stereotype.Service;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.domain.repository.UserRepository;

import java.util.Random;
import java.util.UUID;

@Service
public class LoginService {
    private final UserRepository userRepository;
    private final Random random = new Random();

    public LoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User login(String name) {
        String displayName;
        User user;

        do {
            int id = 1000 + random.nextInt(9000);
            displayName = name + "#" + id;
        } while (userRepository.findByDisplayName(displayName).isPresent());

        user = new User(UUID.randomUUID().toString(), displayName);
        User savedUser = userRepository.save(user);
        System.out.println("User added: " + savedUser);
        return savedUser;
    }
}
