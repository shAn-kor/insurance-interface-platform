package com.insurance.interfaceplatform.domain.common;

public enum FallbackType {
    NONE,
    LAST_SUCCESS_RESPONSE,
    OUTBOX_PENDING,
    STAGING_FILE_RETRY,
    BATCH_RESTART,
    DB_IDEMPOTENCY_FALLBACK,
    AUDIT_OUTBOX,
    DEAD_LETTER
}
