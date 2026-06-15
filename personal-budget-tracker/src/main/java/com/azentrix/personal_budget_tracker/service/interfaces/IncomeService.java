package com.azentrix.personal_budget_tracker.service.interfaces;

import java.util.List;

import com.azentrix.personal_budget_tracker.dto.MonthlySummary;
import com.azentrix.personal_budget_tracker.entity.Income;

public interface IncomeService {
    Income addIncome(Income income, Long userId);

    List<Income> getAllIncome(Long userId);

    Income updateIncome(Long id, Income income, Long userId);

    void deleteIncome(Long id, Long userId);

    MonthlySummary getMonthlySummary(int year, Long userId);
}
