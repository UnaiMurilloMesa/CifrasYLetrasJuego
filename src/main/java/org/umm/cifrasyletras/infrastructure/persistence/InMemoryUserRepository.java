package org.umm.cifrasyletras.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import org.umm.cifrasyletras.domain.model.User;
import org.umm.cifrasyletras.domain.repository.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save (User user) {
        users.put(user.getDisplayName(), user);
        return user;
    }

    @Override
    public Optional<User> findByDisplayName(String displayName) {
        return Optional.ofNullable(users.get(displayName));
    }
}
