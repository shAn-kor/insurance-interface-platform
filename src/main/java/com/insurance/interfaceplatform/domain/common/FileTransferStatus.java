package com.insurance.interfaceplatform.domain.common;

public enum FileTransferStatus {
    READY,
    STREAMING,
    TRANSFER_WAIT,
    TRANSFERRED,
    VERIFY_FAILED,
    QUARANTINED,
    FAILED
}
