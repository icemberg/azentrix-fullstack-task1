package com.azentrix.personal_budget_tracker.repository.interfaces;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import com.azentrix.personal_budget_tracker.entity.Income;

public interface IncomeRepository {
    Income save(Income income);

    List<Income> findAll();

    Optional<Income> findById(Long id);

    void deleteById(Long id);

    List<Income> findByDateBetween(LocalDate start, LocalDate end);

    List<Income> findAllByUserId(Long userId);

    List<Income> findByUserIdAndDateBetween(Long userId, LocalDate start, LocalDate end);

    Optional<Income> findByIdAndUserId(Long id, Long userId);
}
