package com.azentrix.personal_budget_tracker.dto;

import java.time.Instant;

import com.azentrix.personal_budget_tracker.enums.ResponseMessage;
import com.azentrix.personal_budget_tracker.enums.ResponseStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IncomeResponse<T> {
    private int statusCode;
    private ResponseStatus status;
    private ResponseMessage message;
    private T data;
    private Instant timestamp;

    public static <T> IncomeResponse<T> success(ResponseStatus status, ResponseMessage message, T data) {
        return IncomeResponse.<T>builder()
                .statusCode(status.getCode())
                .status(status)
                .message(message)
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> IncomeResponse<T> error(ResponseStatus status, ResponseMessage message) {
        return IncomeResponse.<T>builder()
                .statusCode(status.getCode())
                .status(status)
                .message(message)
                .data(null)
                .timestamp(Instant.now())
                .build();
    }
}
