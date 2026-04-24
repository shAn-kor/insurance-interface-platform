package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import com.insurance.interfaceplatform.domain.common.CircuitBreakerState;
import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.FailureType;
import com.insurance.interfaceplatform.domain.common.FallbackType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "execution_run",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_execution_run_execution_id", columnNames = "execution_id"),
                @UniqueConstraint(name = "uk_execution_run_idempotency_key", columnNames = "idempotency_key")
        }
)
public class ExecutionRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_id", nullable = false, length = 80)
    private String executionId;

    @Column(name = "interface_id", nullable = false)
    private Long interfaceId;

    @Enumerated(EnumType.STRING)
    @Column(name = "execution_type", nullable = false, length = 30)
    private ExecutionType executionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "protocol_type", nullable = false, length = 30)
    private ProtocolType protocolType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ExecutionStatus status;

    @Column(name = "requested_at", nullable = false)
    private Instant requestedAt;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "ended_at")
    private Instant endedAt;

    @Column(name = "duration_millis")
    private Long durationMillis;

    @Column(name = "request_payload_hash", length = 128)
    private String requestPayloadHash;

    @Column(name = "idempotency_key", nullable = false, length = 200)
    private String idempotencyKey;

    @Column(name = "success_yn", nullable = false)
    private Boolean success;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_type", length = 50)
    private FailureType failureType;

    @Column(name = "failure_message", length = 1000)
    private String failureMessage;

    @Column(name = "retryable_yn", nullable = false)
    private Boolean retryable;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Column(name = "parent_execution_id", length = 80)
    private String parentExecutionId;

    @Column(name = "attempt_count", nullable = false)
    private Integer attemptCount;

    @Column(name = "max_attempt_count", nullable = false)
    private Integer maxAttemptCount;

    @Column(name = "next_retry_at")
    private Instant nextRetryAt;

    @Column(name = "fallback_applied_yn", nullable = false)
    private Boolean fallbackApplied;

    @Enumerated(EnumType.STRING)
    @Column(name = "fallback_type", nullable = false, length = 50)
    private FallbackType fallbackType;

    @Enumerated(EnumType.STRING)
    @Column(name = "circuit_breaker_state", nullable = false, length = 30)
    private CircuitBreakerState circuitBreakerState;

    @Column(name = "last_attempt_at")
    private Instant lastAttemptAt;

    protected ExecutionRunEntity() {
    }

    public ExecutionRunEntity(final ExecutionRun executionRun) {
        this.id = executionRun.id();
        this.executionId = executionRun.executionId();
        this.interfaceId = executionRun.interfaceId();
        this.executionType = executionRun.executionType();
        this.protocolType = executionRun.protocolType();
        this.status = executionRun.status();
        this.requestedAt = executionRun.requestedAt();
        this.startedAt = executionRun.startedAt();
        this.endedAt = executionRun.endedAt();
        this.durationMillis = executionRun.durationMillis();
        this.requestPayloadHash = executionRun.requestPayloadHash();
        this.idempotencyKey = executionRun.idempotencyKey();
        this.success = executionRun.success();
        this.failureType = executionRun.failureType();
        this.failureMessage = executionRun.failureMessage();
        this.retryable = executionRun.retryable();
        this.retryCount = executionRun.retryCount();
        this.parentExecutionId = executionRun.parentExecutionId();
        this.attemptCount = executionRun.attemptCount();
        this.maxAttemptCount = executionRun.maxAttemptCount();
        this.nextRetryAt = executionRun.nextRetryAt();
        this.fallbackApplied = executionRun.fallbackApplied();
        this.fallbackType = executionRun.fallbackType();
        this.circuitBreakerState = executionRun.circuitBreakerState();
        this.lastAttemptAt = executionRun.lastAttemptAt();
    }

    public ExecutionRun toDomain() {
        return new ExecutionRun(id, executionId, interfaceId, executionType, protocolType, status, requestedAt,
                startedAt, endedAt, durationMillis, requestPayloadHash, idempotencyKey, success, failureType,
                failureMessage, retryable, retryCount, parentExecutionId, attemptCount, maxAttemptCount,
                nextRetryAt, fallbackApplied, fallbackType, circuitBreakerState, lastAttemptAt);
    }
}
