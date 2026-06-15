package com.azentrix.personal_budget_tracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;

class IncomeServiceImplTest {

    @Test
    void shouldCreateAndReturnEntries() {
        IncomeRepository repository = Mockito.mock(IncomeRepository.class);
        IncomeServiceImpl service = new IncomeServiceImpl(repository);

        Income entry = new Income();
        entry.setType("INCOME");
        entry.setDescription("Salary");
        entry.setAmount(1000.0);
        entry.setCategory("Salary");
        entry.setDate(LocalDate.of(2026, 6, 14));

        when(repository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(repository.findAll()).thenReturn(List.of(entry));

        Income saved = service.addIncome(entry);
        List<Income> entries = service.getAllIncome();

        assertNotNull(saved);
        assertEquals("Salary", saved.getDescription());
        assertEquals(1, entries.size());
        verify(repository).save(entry);
    }
}
