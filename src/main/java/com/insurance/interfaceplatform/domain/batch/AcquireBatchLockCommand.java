package com.insurance.interfaceplatform.domain.batch;

public record AcquireBatchLockCommand(
        String lockKey,
        String lockedBy,
        long lockTtlSeconds
) {
}
