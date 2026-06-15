package com.azentrix.personal_budget_tracker.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ResponseMessage {
    SUCCESS("20000", "Request completed successfully"),
    ENTRY_CREATED("20001", "Entry created successfully"),
    ENTRY_RETRIEVED("20002", "Entries retrieved successfully"),
    ENTRY_UPDATED("20003", "Entry updated successfully"),
    ENTRY_DELETED("20004", "Entry deleted successfully"),
    ENTRY_NOT_FOUND("40001", "Entry not found"),
    INVALID_REQUEST("40002", "Invalid request"),
    INTERNAL_SERVER_ERROR("50000", "Internal server error");

    private final String code;
    private final String message;
}
