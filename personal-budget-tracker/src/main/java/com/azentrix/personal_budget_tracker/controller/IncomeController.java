package com.azentrix.personal_budget_tracker.controller;

import java.time.Year;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.azentrix.personal_budget_tracker.dto.ApiResponse;
import com.azentrix.personal_budget_tracker.dto.MonthlySummary;
import com.azentrix.personal_budget_tracker.entity.Income;
import com.azentrix.personal_budget_tracker.entity.User;
import com.azentrix.personal_budget_tracker.enums.ResponseMessage;
import com.azentrix.personal_budget_tracker.repository.interfaces.UserRepository;
import com.azentrix.personal_budget_tracker.service.interfaces.IncomeService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/v1/income")
@RequiredArgsConstructor
@Slf4j
public class IncomeController {

    private final IncomeService incomeService;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Income>>> getAll(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.ENTRY_RETRIEVED, incomeService.getAllIncome(userId)));
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<MonthlySummary>> getMonthlySummary(
            @RequestParam(required = false) Integer year,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        int resolvedYear = (year != null) ? year : Year.now().getValue();
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.SUCCESS, incomeService.getMonthlySummary(resolvedYear, userId)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Income>> add(
            @RequestBody Income income,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        log.info("Received request to add entry: {}", income);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(ResponseMessage.ENTRY_CREATED, incomeService.addIncome(income, userId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Income>> update(
            @PathVariable Long id,
            @RequestBody Income income,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.ENTRY_UPDATED, incomeService.updateIncome(id, income, userId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        incomeService.deleteIncome(id, userId);
        return ResponseEntity.ok(ApiResponse.success(ResponseMessage.ENTRY_DELETED, null));
    }

    private Long resolveUserId(UserDetails userDetails) {
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return user.getUserId();
    }
}
