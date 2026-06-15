package com.azentrix.personal_budget_tracker.service.interfaces;

import java.util.List;

import com.azentrix.personal_budget_tracker.dto.IncomeDto;
import com.azentrix.personal_budget_tracker.dto.MonthlySummary;
import com.azentrix.personal_budget_tracker.entity.Income;

public interface IncomeService {
    IncomeDto addIncome(Income income, Long userId);

    List<IncomeDto> getAllIncome(Long userId);

    IncomeDto updateIncome(Long id, Income income, Long userId);

    void deleteIncome(Long id, Long userId);

    MonthlySummary getMonthlySummary(int year, Long userId);
}
