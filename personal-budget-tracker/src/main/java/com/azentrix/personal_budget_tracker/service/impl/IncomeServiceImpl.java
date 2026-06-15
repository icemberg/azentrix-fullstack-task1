package com.azentrix.personal_budget_tracker.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;
import com.azentrix.personal_budget_tracker.service.interfaces.IncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncomeServiceImpl implements IncomeService {

    private final IncomeRepository incomeRepository;

    @Override
    public Income addIncome(Income income) {
        log.info("Adding entry: {}", income);
        return incomeRepository.save(income);
    }

    @Override
    public List<Income> getAllIncome() {
        return incomeRepository.findAll();
    }

    @Override
    public Income updateIncome(Long id, Income income) {
        Income existing = incomeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
        existing.setAmount(income.getAmount());
        existing.setDescription(income.getDescription());
        existing.setDate(income.getDate());
        existing.setCategory(income.getCategory());
        existing.setType(income.getType());
        return incomeRepository.save(existing);
    }

    @Override
    public void deleteIncome(Long id) {
        incomeRepository.deleteById(id);
    }
}
