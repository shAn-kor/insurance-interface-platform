package com.insurance.interfaceplatform.domain.batch;

import java.time.Instant;

public record BatchExecutionLock(
        Long id,
        String lockKey,
        String lockedBy,
        Instant lockedUntil,
        Instant createdAt,
        Instant updatedAt
) {
    public static BatchExecutionLock acquire(final AcquireBatchLockCommand command, final Instant now) {
        return new BatchExecutionLock(
                null,
                command.lockKey(),
                command.lockedBy(),
                now.plusSeconds(command.lockTtlSeconds()),
                now,
                now
        );
    }

    public BatchExecutionLock extend(final String nextLockedBy, final long ttlSeconds, final Instant now) {
        return new BatchExecutionLock(id, lockKey, nextLockedBy, now.plusSeconds(ttlSeconds), createdAt, now);
    }
}
