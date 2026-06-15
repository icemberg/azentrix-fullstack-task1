package com.azentrix.personal_budget_tracker.repository.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Repository;

import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;

import jakarta.persistence.EntityManager;

@Repository
public class IncomeRepositoryImpl extends SimpleJpaRepository<Income, Long> implements IncomeRepository {

    public IncomeRepositoryImpl(EntityManager entityManager) {
        super(Income.class, entityManager);
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
}
