package com.azentrix.personal_budget_tracker.repository.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

@Repository
public class IncomeRepositoryImpl extends SimpleJpaRepository<Income, Long> implements IncomeRepository {

    private static final String PARAM_USER_ID = "userId";
    private final EntityManager entityManager;

    public IncomeRepositoryImpl(EntityManager entityManager) {
        super(Income.class, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public Income save(Income income) {
        return super.save(income);
    }

    @Override
    public List<Income> findAll() {
        return super.findAll();
    }

    @Override
    public Optional<Income> findById(Long id) {
        return super.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }

    @Override
    public List<Income> findByDateBetween(LocalDate start, LocalDate end) {
        return entityManager
                .createQuery("SELECT i FROM Income i WHERE i.date BETWEEN :start AND :end ORDER BY i.date",
                        Income.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public List<Income> findAllByUserId(Long userId) {
        return entityManager
                .createQuery("SELECT i FROM Income i WHERE i.user.userId = :userId ORDER BY i.date DESC", Income.class)
                .setParameter(PARAM_USER_ID, userId)
                .getResultList();
    }

    @Override
    public List<Income> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end) {
        return entityManager
                .createQuery(
                        "SELECT i FROM Income i WHERE i.user.userId = :userId AND i.date BETWEEN :start AND :end ORDER BY i.date",
                        Income.class)
                .setParameter(PARAM_USER_ID, userId)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }

    @Override
    public Optional<Income> findByIdAndUserId(Long id, Long userId) {
        try {
            Income income = entityManager
                    .createQuery("SELECT i FROM Income i WHERE i.id = :id AND i.user.userId = :userId", Income.class)
                    .setParameter("id", id)
                    .setParameter(PARAM_USER_ID, userId)
                    .getSingleResult();
            return Optional.of(income);
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
