package com.azentrix.personal_budget_tracker.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlySummary {
    private double totalIncome;
    private double totalExpenses;
    private double balance;
    private List<MonthBreakdown> months;
    private Map<String, Double> incomeByCategory;
    private Map<String, Double> expenseByCategory;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MonthBreakdown {
        private String month;
        private double income;
        private double expenses;
    }
}
