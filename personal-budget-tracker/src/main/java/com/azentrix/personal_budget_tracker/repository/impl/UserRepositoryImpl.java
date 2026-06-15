package com.azentrix.personal_budget_tracker.repository.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import com.azentrix.personal_budget_tracker.entity.User;
import com.azentrix.personal_budget_tracker.repository.interfaces.UserRepository;

import jakarta.persistence.EntityManager;

@Repository
public class UserRepositoryImpl extends SimpleJpaRepository<User, Long> implements UserRepository {

    public UserRepositoryImpl(EntityManager entityManager) {
        super(User.class, entityManager);
    }

    @Override
    public User save(User user) {
        return super.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return super.findById(id);
    }
}
