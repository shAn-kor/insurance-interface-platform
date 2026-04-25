package com.insurance.interfaceplatform.application.batch;

import com.insurance.interfaceplatform.domain.batch.AcquireBatchLockCommand;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLock;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLockRepository;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLockResult;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BatchExecutionLockApplicationService {

    private final BatchExecutionLockRepository batchExecutionLockRepository;
    private final Clock clock;

    @Transactional
    public BatchExecutionLockResult acquire(final AcquireBatchLockCommand command) {
        final Instant now = Instant.now(clock);
        try {
            final BatchExecutionLock saved = batchExecutionLockRepository.save(BatchExecutionLock.acquire(command, now));
            return BatchExecutionLockResult.acquired(saved.lockKey());
        } catch (final DataIntegrityViolationException exception) {
            final Instant lockedUntil = now.plusSeconds(command.lockTtlSeconds());
            final int updatedCount = batchExecutionLockRepository.acquireExpired(command.lockKey(), command.lockedBy(), lockedUntil, now);
            if (updatedCount == 1) {
                return BatchExecutionLockResult.acquired(command.lockKey());
            }
            return BatchExecutionLockResult.rejected(command.lockKey());
        }
    }

    @Transactional
    public void release(final String lockKey, final String lockedBy) {
        batchExecutionLockRepository.release(lockKey, lockedBy);
    }
}
