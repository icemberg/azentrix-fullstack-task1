package com.azentrix.personal_budget_tracker.service.impl;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.azentrix.personal_budget_tracker.dto.MonthlySummary;
import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.entity.User;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;
import com.azentrix.personal_budget_tracker.repository.interfaces.UserRepository;
import com.azentrix.personal_budget_tracker.service.interfaces.IncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class IncomeServiceImpl implements IncomeService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    private final IncomeRepository incomeRepository;
    private final UserRepository userRepository;

    @Override
    public Income addIncome(Income income, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        income.setUser(user);
        log.info("Adding entry for user {}: {}", userId, income);
        return incomeRepository.save(income);
    }

    @Override
    public List<Income> getAllIncome(Long userId) {
        return incomeRepository.findAllByUserId(userId);
    }

    @Override
    public Income updateIncome(Long id, Income income, Long userId) {
        Income existing = incomeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
        existing.setAmount(income.getAmount());
        existing.setDescription(income.getDescription());
        existing.setDate(income.getDate());
        existing.setCategory(income.getCategory());
        existing.setType(income.getType());
        return incomeRepository.save(existing);
    }

    @Override
    public void deleteIncome(Long id, Long userId) {
        Income existing = incomeRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new IllegalArgumentException("Entry not found"));
        incomeRepository.deleteById(existing.getId());
    }

    @Override
    public MonthlySummary getMonthlySummary(int year, Long userId) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);
        List<Income> entries = incomeRepository.findByUserIdAndDateBetween(userId, start, end);

        Map<String, Double> monthlyIncome = new TreeMap<>();
        Map<String, Double> monthlyExpense = new TreeMap<>();

        for (int m = 1; m <= 12; m++) {
            String key = YearMonth.of(year, m).format(MONTH_FORMATTER);
            monthlyIncome.put(key, 0.0);
            monthlyExpense.put(key, 0.0);
        }

        for (Income entry : entries) {
            String key = entry.getDate().format(MONTH_FORMATTER);
            if ("INCOME".equalsIgnoreCase(entry.getType())) {
                monthlyIncome.merge(key, entry.getAmount(), Double::sum);
            } else {
                monthlyExpense.merge(key, entry.getAmount(), Double::sum);
            }
        }

        List<MonthlySummary.MonthBreakdown> months = monthlyIncome.keySet().stream()
                .map(key -> MonthlySummary.MonthBreakdown.builder()
                        .month(key)
                        .income(monthlyIncome.get(key))
                        .expenses(monthlyExpense.get(key))
                        .build())
                .toList();

        double totalIncome = months.stream().mapToDouble(MonthlySummary.MonthBreakdown::getIncome).sum();
        double totalExpenses = months.stream().mapToDouble(MonthlySummary.MonthBreakdown::getExpenses).sum();

        Map<String, Double> incomeByCategory = entries.stream()
                .filter(e -> "INCOME".equalsIgnoreCase(e.getType()))
                .collect(Collectors.groupingBy(
                        Income::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Income::getAmount)));

        Map<String, Double> expenseByCategory = entries.stream()
                .filter(e -> "EXPENSE".equalsIgnoreCase(e.getType()))
                .collect(Collectors.groupingBy(
                        Income::getCategory,
                        LinkedHashMap::new,
                        Collectors.summingDouble(Income::getAmount)));

        return MonthlySummary.builder()
                .totalIncome(totalIncome)
                .totalExpenses(totalExpenses)
                .balance(totalIncome - totalExpenses)
                .months(months)
                .incomeByCategory(incomeByCategory)
                .expenseByCategory(expenseByCategory)
                .build();
    }
}
