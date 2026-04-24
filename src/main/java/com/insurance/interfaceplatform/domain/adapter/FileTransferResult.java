package com.insurance.interfaceplatform.domain.adapter;

import com.insurance.interfaceplatform.domain.common.FileTransferStatus;

public record FileTransferResult(
        FileTransferStatus status,
        String fileName,
        String remotePath,
        long fileSize,
        String checksumAlgorithm,
        String checksumValue
) {
}
