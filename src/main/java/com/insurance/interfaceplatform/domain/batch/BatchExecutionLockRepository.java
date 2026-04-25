package com.insurance.interfaceplatform.domain.batch;

import java.time.Instant;
import java.util.Optional;

public interface BatchExecutionLockRepository {

    BatchExecutionLock save(BatchExecutionLock batchExecutionLock);

    Optional<BatchExecutionLock> findByLockKey(String lockKey);

    int acquireExpired(String lockKey, String lockedBy, Instant lockedUntil, Instant now);

    int release(String lockKey, String lockedBy);
}
