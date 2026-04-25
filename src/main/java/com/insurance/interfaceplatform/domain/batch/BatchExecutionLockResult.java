package com.insurance.interfaceplatform.domain.batch;

public record BatchExecutionLockResult(
        boolean acquired,
        String lockKey
) {
    public static BatchExecutionLockResult acquired(final String lockKey) {
        return new BatchExecutionLockResult(true, lockKey);
    }

    public static BatchExecutionLockResult rejected(final String lockKey) {
        return new BatchExecutionLockResult(false, lockKey);
    }
}
