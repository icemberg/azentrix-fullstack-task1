package com.azentrix.personal_budget_tracker.dto;

import java.time.Instant;

import com.azentrix.personal_budget_tracker.enums.ResponseMessage;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private int statusCode;
    private String code;
    private String message;
    private T data;
    private Instant timestamp;

    public static <T> ApiResponse<T> success(ResponseMessage msg, T data) {
        return ApiResponse.<T>builder()
                .statusCode(msg.getHttpStatus())
                .code(msg.getCode())
                .message(msg.getMessage())
                .data(data)
                .timestamp(Instant.now())
                .build();
    }

    public static <T> ApiResponse<T> error(ResponseMessage msg) {
        return ApiResponse.<T>builder()
                .statusCode(msg.getHttpStatus())
                .code(msg.getCode())
                .message(msg.getMessage())
                .data(null)
                .timestamp(Instant.now())
                .build();
    }
}
