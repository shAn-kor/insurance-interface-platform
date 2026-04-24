package com.insurance.interfaceplatform.domain.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import java.time.Instant;

public record ExecutionStatusHistory(
        Long id,
        String executionId,
        ExecutionStatus fromStatus,
        ExecutionStatus toStatus,
        String reason,
        Instant changedAt
) {

    public static ExecutionStatusHistory record(
            final String executionId,
            final ExecutionStatus fromStatus,
            final ExecutionStatus toStatus,
            final String reason,
            final Instant changedAt
    ) {
        return new ExecutionStatusHistory(null, executionId, fromStatus, toStatus, reason, changedAt);
    }
}
