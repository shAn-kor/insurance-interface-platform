package com.insurance.interfaceplatform.application.batch;

import com.insurance.interfaceplatform.domain.batch.BatchExecutionLock;
import com.insurance.interfaceplatform.domain.batch.BatchExecutionLockRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

public class InMemoryBatchExecutionLockRepository implements BatchExecutionLockRepository {

    private final Map<String, BatchExecutionLock> store = new HashMap<>();
    private long sequence = 1L;

    @Override
    public BatchExecutionLock save(final BatchExecutionLock batchExecutionLock) {
        if (batchExecutionLock.id() == null && store.containsKey(batchExecutionLock.lockKey())) {
            throw new DataIntegrityViolationException("duplicated lock");
        }
        final BatchExecutionLock saved = new BatchExecutionLock(
                batchExecutionLock.id() == null ? sequence++ : batchExecutionLock.id(),
                batchExecutionLock.lockKey(),
                batchExecutionLock.lockedBy(),
                batchExecutionLock.lockedUntil(),
                batchExecutionLock.createdAt(),
                batchExecutionLock.updatedAt()
        );
        store.put(saved.lockKey(), saved);
        return saved;
    }

    @Override
    public Optional<BatchExecutionLock> findByLockKey(final String lockKey) {
        return Optional.ofNullable(store.get(lockKey));
    }

    @Override
    public int acquireExpired(
            final String lockKey,
            final String lockedBy,
            final Instant lockedUntil,
            final Instant now
    ) {
        final BatchExecutionLock found = store.get(lockKey);
        if (found == null || !found.lockedUntil().isBefore(now)) {
            return 0;
        }
        store.put(lockKey, new BatchExecutionLock(found.id(), lockKey, lockedBy, lockedUntil, found.createdAt(), now));
        return 1;
    }

    @Override
    public int release(final String lockKey, final String lockedBy) {
        final BatchExecutionLock found = store.get(lockKey);
        if (found == null || !found.lockedBy().equals(lockedBy)) {
            return 0;
        }
        store.remove(lockKey);
        return 1;
    }
}
