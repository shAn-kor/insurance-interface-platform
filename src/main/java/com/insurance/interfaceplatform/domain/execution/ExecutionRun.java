package com.insurance.interfaceplatform.domain.execution;

import com.insurance.interfaceplatform.domain.common.CircuitBreakerState;
import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.FailureType;
import com.insurance.interfaceplatform.domain.common.FallbackType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import java.time.Instant;

public record ExecutionRun(
        Long id,
        String executionId,
        Long interfaceId,
        ExecutionType executionType,
        ProtocolType protocolType,
        ExecutionStatus status,
        Instant requestedAt,
        Instant startedAt,
        Instant endedAt,
        Long durationMillis,
        String requestPayloadHash,
        String idempotencyKey,
        Boolean success,
        FailureType failureType,
        String failureMessage,
        Boolean retryable,
        Integer retryCount,
        String parentExecutionId,
        Integer attemptCount,
        Integer maxAttemptCount,
        Instant nextRetryAt,
        Boolean fallbackApplied,
        FallbackType fallbackType,
        CircuitBreakerState circuitBreakerState,
        Instant lastAttemptAt
) {

    public static ExecutionRun create(final String executionId, final CreateExecutionCommand command, final Instant requestedAt) {
        return new ExecutionRun(
                null,
                executionId,
                command.interfaceId(),
                command.executionType(),
                command.protocolType(),
                ExecutionStatus.REQUESTED,
                requestedAt,
                null,
                null,
                null,
                command.requestPayloadHash(),
                command.idempotencyKey(),
                false,
                null,
                null,
                false,
                0,
                null,
                0,
                0,
                null,
                false,
                FallbackType.NONE,
                CircuitBreakerState.CLOSED,
                null
        );
    }

    public ExecutionRun transit(final ExecutionStatus nextStatus, final Instant changedAt) {
        final Instant nextStartedAt = status == ExecutionStatus.REQUESTED && nextStatus == ExecutionStatus.PROCESSING ? changedAt : startedAt;
        final Instant nextEndedAt = isFinishedStatus(nextStatus) ? changedAt : endedAt;
        final Long nextDurationMillis = nextStartedAt != null && nextEndedAt != null
                ? Long.valueOf(nextEndedAt.toEpochMilli() - nextStartedAt.toEpochMilli())
                : durationMillis;
        final Boolean nextSuccess = nextStatus == ExecutionStatus.SUCCESS;
        return new ExecutionRun(id, executionId, interfaceId, executionType, protocolType, nextStatus, requestedAt,
                nextStartedAt, nextEndedAt, nextDurationMillis, requestPayloadHash, idempotencyKey, nextSuccess,
                failureType, failureMessage, retryable, retryCount, parentExecutionId, attemptCount, maxAttemptCount,
                nextRetryAt, fallbackApplied, fallbackType, circuitBreakerState, changedAt);
    }

    public ExecutionRun fail(final FailureType nextFailureType, final String nextFailureMessage, final boolean nextRetryable, final Instant failedAt) {
        return new ExecutionRun(id, executionId, interfaceId, executionType, protocolType, ExecutionStatus.FAILED, requestedAt,
                startedAt, failedAt, startedAt == null ? null : failedAt.toEpochMilli() - startedAt.toEpochMilli(),
                requestPayloadHash, idempotencyKey, false, nextFailureType, nextFailureMessage, nextRetryable,
                retryCount, parentExecutionId, attemptCount, maxAttemptCount, nextRetryAt, fallbackApplied,
                fallbackType, circuitBreakerState, failedAt);
    }

    public ExecutionRun waitRetry(final Instant nextRetryAt, final int nextAttemptCount, final int nextMaxAttemptCount, final Instant changedAt) {
        return new ExecutionRun(id, executionId, interfaceId, executionType, protocolType, ExecutionStatus.RETRY_WAIT,
                requestedAt, startedAt, endedAt, durationMillis, requestPayloadHash, idempotencyKey, false,
                failureType, failureMessage, true, retryCount + 1, parentExecutionId, nextAttemptCount,
                nextMaxAttemptCount, nextRetryAt, fallbackApplied, fallbackType, circuitBreakerState, changedAt);
    }

    public ExecutionRun deadLetter(final FallbackType nextFallbackType, final Instant deadLetteredAt) {
        return new ExecutionRun(id, executionId, interfaceId, executionType, protocolType, ExecutionStatus.DEAD_LETTER,
                requestedAt, startedAt, deadLetteredAt, durationMillis, requestPayloadHash, idempotencyKey, false,
                failureType, failureMessage, false, retryCount, parentExecutionId, attemptCount, maxAttemptCount,
                nextRetryAt, true, nextFallbackType, circuitBreakerState, deadLetteredAt);
    }

    public boolean isFinishedStatus(final ExecutionStatus targetStatus) {
        return targetStatus == ExecutionStatus.SUCCESS || targetStatus == ExecutionStatus.FAILED
                || targetStatus == ExecutionStatus.DEAD_LETTER || targetStatus == ExecutionStatus.CANCELLED;
    }
}
