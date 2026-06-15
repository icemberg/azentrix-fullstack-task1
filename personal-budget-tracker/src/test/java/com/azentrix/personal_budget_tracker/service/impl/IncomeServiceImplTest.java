package com.azentrix.personal_budget_tracker.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.azentrix.personal_budget_tracker.dto.IncomeDto;
import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.entity.User;
import com.azentrix.personal_budget_tracker.repository.interfaces.IncomeRepository;
import com.azentrix.personal_budget_tracker.repository.interfaces.UserRepository;

class IncomeServiceImplTest {

    @Test
    void shouldCreateAndReturnEntries() {
        IncomeRepository incomeRepository = Mockito.mock(IncomeRepository.class);
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        IncomeServiceImpl service = new IncomeServiceImpl(incomeRepository, userRepository);

        Long testUserId = 1L;
        User testUser = new User();
        testUser.setUserId(testUserId);
        
        Income entry = new Income();
        entry.setId(1L);
        entry.setType("INCOME");
        entry.setDescription("Salary");
        entry.setAmount(1000.0);
        entry.setCategory("Salary");
        entry.setDate(LocalDate.of(2026, 6, 14));

        when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
        when(incomeRepository.save(any(Income.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(incomeRepository.findAllByUserId(testUserId)).thenReturn(List.of(entry));

        IncomeDto saved = service.addIncome(entry, testUserId);
        List<IncomeDto> entries = service.getAllIncome(testUserId);

        assertNotNull(saved);
        assertEquals("Salary", saved.getDescription());
        assertEquals(1, entries.size());
        verify(incomeRepository).save(entry);
    }
}
