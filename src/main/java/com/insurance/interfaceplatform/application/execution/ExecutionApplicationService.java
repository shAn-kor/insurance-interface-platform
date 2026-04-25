package com.insurance.interfaceplatform.application.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.common.FailureType;
import com.insurance.interfaceplatform.domain.common.FallbackType;
import com.insurance.interfaceplatform.domain.execution.CreateExecutionCommand;
import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import com.insurance.interfaceplatform.domain.execution.ExecutionRunRepository;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistory;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistoryRepository;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusTransitionPolicy;
import com.insurance.interfaceplatform.support.error.CoreException;
import com.insurance.interfaceplatform.support.error.ErrorType;
import java.time.Clock;
import java.time.Instant;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExecutionApplicationService {

    private final ExecutionRunRepository executionRunRepository;
    private final ExecutionStatusHistoryRepository executionStatusHistoryRepository;
    private final ExecutionStatusTransitionPolicy executionStatusTransitionPolicy;
    private final ExecutionIdGenerator executionIdGenerator;
    private final Clock clock;

    public ExecutionApplicationService(
            final ExecutionRunRepository executionRunRepository,
            final ExecutionStatusHistoryRepository executionStatusHistoryRepository,
            final ExecutionStatusTransitionPolicy executionStatusTransitionPolicy,
            final ExecutionIdGenerator executionIdGenerator,
            final Clock clock
    ) {
        this.executionRunRepository = executionRunRepository;
        this.executionStatusHistoryRepository = executionStatusHistoryRepository;
        this.executionStatusTransitionPolicy = executionStatusTransitionPolicy;
        this.executionIdGenerator = executionIdGenerator;
        this.clock = clock;
    }

    @Transactional
    public ExecutionRun create(final CreateExecutionCommand command) {
        final Instant now = Instant.now(clock);
        final String executionId = executionIdGenerator.generate();
        final ExecutionRun executionRun = ExecutionRun.create(executionId, command, now);
        final ExecutionRun saved = executionRunRepository.save(executionRun);
        executionStatusHistoryRepository.save(ExecutionStatusHistory.record(saved.executionId(), null, saved.status(), "실행 요청 생성", now));
        return saved;
    }

    @Transactional(readOnly = true)
    public ExecutionRun get(final String executionId) {
        return executionRunRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "실행 이력을 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<ExecutionRun> list(final Pageable pageable) {
        return executionRunRepository.findAll(pageable);
    }

    @Transactional
    public ExecutionRun changeStatus(final String executionId, final ExecutionStatus nextStatus, final String reason) {
        final ExecutionRun found = executionRunRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "실행 이력을 찾을 수 없습니다."));
        executionStatusTransitionPolicy.validate(found.status(), nextStatus);
        final Instant now = Instant.now(clock);
        final ExecutionRun updated = found.transit(nextStatus, now);
        updateExecutionRunOrThrow(updated, found.status());
        executionStatusHistoryRepository.save(ExecutionStatusHistory.record(executionId, found.status(), nextStatus, reason, now));
        return updated;
    }

    @Transactional
    public ExecutionRun recordFailure(
            final String executionId,
            final FailureType failureType,
            final String failureMessage,
            final boolean retryable
    ) {
        final ExecutionRun found = executionRunRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "실행 이력을 찾을 수 없습니다."));
        executionStatusTransitionPolicy.validate(found.status(), ExecutionStatus.FAILED);
        final Instant now = Instant.now(clock);
        final ExecutionRun failed = found.fail(failureType, failureMessage, retryable, now);
        updateExecutionRunOrThrow(failed, found.status());
        executionStatusHistoryRepository.save(ExecutionStatusHistory.record(executionId, found.status(), ExecutionStatus.FAILED, failureMessage, now));
        return failed;
    }

    @Transactional
    public ExecutionRun waitRetry(final String executionId, final Instant nextRetryAt, final int attemptCount, final int maxAttemptCount) {
        final ExecutionRun found = executionRunRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "실행 이력을 찾을 수 없습니다."));
        executionStatusTransitionPolicy.validate(found.status(), ExecutionStatus.RETRY_WAIT);
        final ExecutionRun retryWait = found.waitRetry(nextRetryAt, attemptCount, maxAttemptCount, Instant.now(clock));
        updateExecutionRunOrThrow(retryWait, found.status());
        executionStatusHistoryRepository.save(ExecutionStatusHistory.record(executionId, found.status(), ExecutionStatus.RETRY_WAIT, "재시도 대기 전환", Instant.now(clock)));
        return retryWait;
    }

    @Transactional
    public ExecutionRun deadLetter(final String executionId, final FallbackType fallbackType) {
        final ExecutionRun found = executionRunRepository.findByExecutionId(executionId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "실행 이력을 찾을 수 없습니다."));
        executionStatusTransitionPolicy.validate(found.status(), ExecutionStatus.DEAD_LETTER);
        final Instant now = Instant.now(clock);
        final ExecutionRun deadLettered = found.deadLetter(fallbackType, now);
        updateExecutionRunOrThrow(deadLettered, found.status());
        executionStatusHistoryRepository.save(ExecutionStatusHistory.record(executionId, found.status(), ExecutionStatus.DEAD_LETTER, "Dead Letter 전환", now));
        return deadLettered;
    }

    public void updateExecutionRunOrThrow(final ExecutionRun executionRun, final ExecutionStatus expectedStatus) {
        final int updatedCount = executionRunRepository.updateIfStatus(executionRun, expectedStatus);
        if (updatedCount != 1) {
            throw new CoreException(ErrorType.EXECUTION_STATUS_CONFLICT, "동시에 다른 요청이 실행 상태를 먼저 변경했습니다.");
        }
    }
}
