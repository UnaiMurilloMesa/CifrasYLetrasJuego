package org.umm.cifrasyletras.domain.repository;

import org.springframework.stereotype.Repository;
import org.umm.cifrasyletras.domain.model.User;

import java.util.Optional;

public interface UserRepository {
    User save (User user);
    Optional<User> findByDisplayName(String displayName);
}
