package com.insurance.interfaceplatform.domain.common;

public enum ExecutionStatus {
    REQUESTED,
    VALIDATED,
    DISPATCHED,
    PROCESSING,
    SUCCESS,
    FAILED,
    RETRY_WAIT,
    RETRYING,
    DEAD_LETTER,
    CANCELLED
}
