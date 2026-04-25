package com.insurance.interfaceplatform.infrastructure.persistence.batch;

import com.insurance.interfaceplatform.domain.batch.BatchExecutionLock;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLockRepository;
import java.time.Instant;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BatchExecutionLockJpaAdapter implements BatchExecutionLockRepository {

    private final BatchExecutionLockJpaRepository batchExecutionLockJpaRepository;

    @Override
    public BatchExecutionLock save(final BatchExecutionLock batchExecutionLock) {
        return batchExecutionLockJpaRepository.saveAndFlush(new BatchExecutionLockEntity(batchExecutionLock)).toDomain();
    }

    @Override
    public Optional<BatchExecutionLock> findByLockKey(final String lockKey) {
        return batchExecutionLockJpaRepository.findByLockKey(lockKey)
                .map(BatchExecutionLockEntity::toDomain);
    }

    @Override
    public int acquireExpired(final String lockKey, final String lockedBy, final Instant lockedUntil, final Instant now) {
        return batchExecutionLockJpaRepository.acquireExpired(lockKey, lockedBy, lockedUntil, now);
    }

    @Override
    public int release(final String lockKey, final String lockedBy) {
        return batchExecutionLockJpaRepository.release(lockKey, lockedBy);
    }
}
