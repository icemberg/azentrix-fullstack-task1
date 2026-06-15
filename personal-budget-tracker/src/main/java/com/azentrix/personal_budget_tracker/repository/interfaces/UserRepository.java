package com.azentrix.personal_budget_tracker.repository.interfaces;

import java.util.Optional;

import com.azentrix.personal_budget_tracker.entity.User;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);
}
