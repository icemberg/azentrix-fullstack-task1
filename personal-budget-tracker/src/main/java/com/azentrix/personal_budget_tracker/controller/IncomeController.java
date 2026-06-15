package com.azentrix.personal_budget_tracker.controller;

import java.time.Year;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azentrix.personal_budget_tracker.dto.IncomeResponse;
import com.azentrix.personal_budget_tracker.dto.MonthlySummary;
import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.enums.ResponseMessage;
import com.azentrix.personal_budget_tracker.enums.ResponseStatus;
import com.azentrix.personal_budget_tracker.service.interfaces.IncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/income")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class IncomeController {

    private final IncomeService incomeService;

    @GetMapping
    public ResponseEntity<IncomeResponse<List<Income>>> getAll() {
        return ResponseEntity.ok(IncomeResponse.success(ResponseStatus.SUCCESS, ResponseMessage.ENTRY_RETRIEVED, incomeService.getAllIncome()));
    }

    @GetMapping("/summary")
    public ResponseEntity<IncomeResponse<MonthlySummary>> getMonthlySummary(
            @RequestParam(required = false) Integer year) {
        int resolvedYear = (year != null) ? year : Year.now().getValue();
        return ResponseEntity.ok(IncomeResponse.success(ResponseStatus.SUCCESS, ResponseMessage.SUCCESS, incomeService.getMonthlySummary(resolvedYear)));
    }

    @PostMapping
    public ResponseEntity<IncomeResponse<Income>> add(@RequestBody Income income) {
        log.info("Received request to add entry: {}", income);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IncomeResponse.success(ResponseStatus.CREATED, ResponseMessage.ENTRY_CREATED, incomeService.addIncome(income)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<IncomeResponse<Income>> update(@PathVariable Long id, @RequestBody Income income) {
        return ResponseEntity.ok(IncomeResponse.success(ResponseStatus.SUCCESS, ResponseMessage.ENTRY_UPDATED, incomeService.updateIncome(id, income)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<IncomeResponse<Void>> delete(@PathVariable Long id) {
        incomeService.deleteIncome(id);
        return ResponseEntity.ok(IncomeResponse.success(ResponseStatus.SUCCESS, ResponseMessage.ENTRY_DELETED, null));
    }
}
