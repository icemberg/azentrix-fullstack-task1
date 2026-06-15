package com.azentrix.personal_budget_tracker.service.interfaces;

import java.util.List;

import com.azentrix.personal_budget_tracker.entity.Income;

public interface IncomeService {
    Income addIncome(Income income);

    List<Income> getAllIncome();

    Income updateIncome(Long id, Income income);

    void deleteIncome(Long id);
}
