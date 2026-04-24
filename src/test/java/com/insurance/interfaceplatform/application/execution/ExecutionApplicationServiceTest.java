package com.insurance.interfaceplatform.application.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.FailureType;
import com.insurance.interfaceplatform.domain.common.FallbackType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.domain.execution.CreateExecutionCommand;
import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import com.insurance.interfaceplatform.domain.execution.ExecutionRunRepository;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistory;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistoryRepository;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusTransitionPolicy;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExecutionApplicationServiceTest {

    @Mock
    private ExecutionRunRepository executionRunRepository;

    @Mock
    private ExecutionStatusHistoryRepository executionStatusHistoryRepository;

    @Mock
    private ExecutionIdGenerator executionIdGenerator;

    private final ExecutionStatusTransitionPolicy executionStatusTransitionPolicy = new ExecutionStatusTransitionPolicy();
    private final Clock clock = Clock.fixed(Instant.parse("2026-04-24T10:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("create_정상요청_실행이력과상태이력을저장한다")
    void create_validCommand_savesExecutionAndHistory() {
        final ExecutionApplicationService service = service();
        when(executionIdGenerator.generate()).thenReturn("EXE-20260424-000001");
        when(executionRunRepository.save(any(ExecutionRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(executionStatusHistoryRepository.save(any(ExecutionStatusHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final ExecutionRun result = service.create(command());

        assertThat(result.executionId()).isEqualTo("EXE-20260424-000001");
        assertThat(result.status()).isEqualTo(ExecutionStatus.REQUESTED);
        verify(executionRunRepository).save(any(ExecutionRun.class));
        verify(executionStatusHistoryRepository).save(any(ExecutionStatusHistory.class));
    }

    @Test
    @DisplayName("changeStatus_허용된상태전이_상태를변경한다")
    void changeStatus_allowedTransition_changesStatus() {
        final ExecutionApplicationService service = service();
        final ExecutionRun requested = ExecutionRun.create("EXE-20260424-000001", command(), Instant.now(clock));
        when(executionRunRepository.findByExecutionId("EXE-20260424-000001")).thenReturn(Optional.of(requested));
        when(executionRunRepository.save(any(ExecutionRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(executionStatusHistoryRepository.save(any(ExecutionStatusHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final ExecutionRun result = service.changeStatus("EXE-20260424-000001", ExecutionStatus.PROCESSING, "처리 시작");

        assertThat(result.status()).isEqualTo(ExecutionStatus.PROCESSING);
    }

    @Test
    @DisplayName("changeStatus_종료상태에서처리중전이_예외가발생한다")
    void changeStatus_finishedStatusToProcessing_throwsException() {
        final ExecutionApplicationService service = service();
        final ExecutionRun success = ExecutionRun.create("EXE-20260424-000001", command(), Instant.now(clock))
                .transit(ExecutionStatus.PROCESSING, Instant.now(clock))
                .transit(ExecutionStatus.SUCCESS, Instant.now(clock));
        when(executionRunRepository.findByExecutionId("EXE-20260424-000001")).thenReturn(Optional.of(success));

        assertThatThrownBy(() -> service.changeStatus("EXE-20260424-000001", ExecutionStatus.PROCESSING, "재처리"))
                .hasMessageContaining("허용되지 않는 실행 상태 전이");
    }

    @Test
    @DisplayName("deadLetter_재시도대기상태_DeadLetter로전환한다")
    void deadLetter_retryWaitStatus_changesToDeadLetter() {
        final ExecutionApplicationService service = service();
        final ExecutionRun retryWait = ExecutionRun.create("EXE-20260424-000001", command(), Instant.now(clock))
                .transit(ExecutionStatus.PROCESSING, Instant.now(clock))
                .fail(FailureType.TIMEOUT, "timeout", true, Instant.now(clock))
                .waitRetry(Instant.now(clock).plusSeconds(60), 1, 3);
        when(executionRunRepository.findByExecutionId("EXE-20260424-000001")).thenReturn(Optional.of(retryWait));
        when(executionRunRepository.save(any(ExecutionRun.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(executionStatusHistoryRepository.save(any(ExecutionStatusHistory.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final ExecutionRun result = service.deadLetter("EXE-20260424-000001", FallbackType.DEAD_LETTER);

        assertThat(result.status()).isEqualTo(ExecutionStatus.DEAD_LETTER);
        assertThat(result.fallbackApplied()).isTrue();
    }

    private ExecutionApplicationService service() {
        return new ExecutionApplicationService(
                executionRunRepository,
                executionStatusHistoryRepository,
                executionStatusTransitionPolicy,
                executionIdGenerator,
                clock
        );
    }

    private CreateExecutionCommand command() {
        return new CreateExecutionCommand(1L, ExecutionType.MANUAL, ProtocolType.REST,
                "CONTRACT-20260424-FSS-001", "hash", "payload summary");
    }
}
