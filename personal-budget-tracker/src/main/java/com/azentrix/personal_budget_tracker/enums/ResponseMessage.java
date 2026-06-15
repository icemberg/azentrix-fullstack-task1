package com.azentrix.personal_budget_tracker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {
    SUCCESS("20000", "Request completed successfully", 200),
    ENTRY_CREATED("20001", "Entry created successfully", 201),
    ENTRY_RETRIEVED("20002", "Entries retrieved successfully", 200),
    ENTRY_UPDATED("20003", "Entry updated successfully", 200),
    ENTRY_DELETED("20004", "Entry deleted successfully", 200),
    USER_REGISTERED("20005", "User registered successfully", 201),
    LOGIN_SUCCESS("20006", "Login successful", 200),
    ENTRY_NOT_FOUND("40001", "Entry not found", 404),
    INVALID_REQUEST("40002", "Invalid request", 400),
    UNAUTHORIZED("40101", "Invalid credentials", 401),
    TOKEN_EXPIRED("40102", "Token has expired", 401),
    USER_ALREADY_EXISTS("40901", "Username already taken", 409),
    INTERNAL_SERVER_ERROR("50000", "Internal server error", 500);

    private final String code;
    private final String message;
    private final int httpStatus;
}
