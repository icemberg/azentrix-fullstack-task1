package com.azentrix.personal_budget_tracker.repository.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;

import jakarta.persistence.EntityManager;

@Repository
public class IncomeRepositoryImpl extends SimpleJpaRepository<Income, Long> implements IncomeRepository {

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
                .createQuery("SELECT i FROM Income i WHERE i.date BETWEEN :start AND :end ORDER BY i.date", Income.class)
                .setParameter("start", start)
                .setParameter("end", end)
                .getResultList();
    }
}
