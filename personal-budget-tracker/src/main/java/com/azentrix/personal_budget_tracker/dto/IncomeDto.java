package com.azentrix.personal_budget_tracker.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeDto {
    private Long id;
    private Double amount;
    private String description;
    private LocalDate date;
    private String category;
    private String type;
}
