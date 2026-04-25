package com.insurance.interfaceplatform.application.batch;

import com.insurance.interfaceplatform.domain.batch.AcquireBatchLockCommand;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLock;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLockResult;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BatchExecutionLockApplicationServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-04-25T10:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("acquire_신규락_획득성공")
    void acquire_newLock_returnsAcquired() {
        final InMemoryBatchExecutionLockRepository repository = new InMemoryBatchExecutionLockRepository();
        final BatchExecutionLockApplicationService service = new BatchExecutionLockApplicationService(repository, clock);

        final BatchExecutionLockResult result = service.acquire(command("worker-1"));

        assertThat(result.acquired()).isTrue();
        assertThat(result.lockKey()).isEqualTo("BATCH:1:fssContractStatisticsJob:2026-04-25");
    }

    @Test
    @DisplayName("acquire_이미유효한락존재_획득거부")
    void acquire_activeLock_returnsRejected() {
        final InMemoryBatchExecutionLockRepository repository = new InMemoryBatchExecutionLockRepository();
        final BatchExecutionLockApplicationService service = new BatchExecutionLockApplicationService(repository, clock);
        service.acquire(command("worker-1"));

        final BatchExecutionLockResult result = service.acquire(command("worker-2"));

        assertThat(result.acquired()).isFalse();
    }

    @Test
    @DisplayName("acquire_만료된락존재_새작업자가재획득")
    void acquire_expiredLock_returnsAcquired() {
        final InMemoryBatchExecutionLockRepository repository = new InMemoryBatchExecutionLockRepository();
        final BatchExecutionLockApplicationService service = new BatchExecutionLockApplicationService(repository, clock);
        repository.save(new BatchExecutionLock(1L, "BATCH:1:fssContractStatisticsJob:2026-04-25", "worker-old",
                Instant.parse("2026-04-25T09:59:00Z"), Instant.parse("2026-04-25T09:00:00Z"), Instant.parse("2026-04-25T09:00:00Z")));

        final BatchExecutionLockResult result = service.acquire(command("worker-new"));

        assertThat(result.acquired()).isTrue();
        final BatchExecutionLock lock = repository.findByLockKey("BATCH:1:fssContractStatisticsJob:2026-04-25").orElseThrow();
        assertThat(lock.lockedBy()).isEqualTo("worker-new");
    }

    @Test
    @DisplayName("release_락소유자일치_락해제")
    void release_ownerMatched_removesLock() {
        final InMemoryBatchExecutionLockRepository repository = new InMemoryBatchExecutionLockRepository();
        final BatchExecutionLockApplicationService service = new BatchExecutionLockApplicationService(repository, clock);
        service.acquire(command("worker-1"));

        service.release("BATCH:1:fssContractStatisticsJob:2026-04-25", "worker-1");

        assertThat(repository.findByLockKey("BATCH:1:fssContractStatisticsJob:2026-04-25")).isEmpty();
    }

    private AcquireBatchLockCommand command(final String lockedBy) {
        return new AcquireBatchLockCommand("BATCH:1:fssContractStatisticsJob:2026-04-25", lockedBy, 300);
    }
}
