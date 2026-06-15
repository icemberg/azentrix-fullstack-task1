package com.azentrix.personal_budget_tracker.repository.impl;

import java.util.Optional;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import com.azentrix.personal_budget_tracker.entity.User;
import com.azentrix.personal_budget_tracker.repository.interfaces.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class UserRepositoryImpl extends SimpleJpaRepository<User, Long> implements UserRepository {

    private final EntityManager entityManager;

    public UserRepositoryImpl(EntityManager entityManager) {
        super(User.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public User save(User user) {
        return super.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        try {
            User user = entityManager
                    .createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();
            return Optional.of(user);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        Long count = entityManager
                .createQuery("SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class)
                .setParameter("username", username)
                .getSingleResult();
        return count > 0;
    }
}
