package com.insurance.interfaceplatform.domain.adapter;

import com.insurance.interfaceplatform.domain.common.TransferDirection;
import java.nio.file.Path;

public record FileTransferCommand(
        Long interfaceId,
        String executionId,
        TransferDirection transferDirection,
        Path stagingPath,
        String remotePath,
        String tempFileName,
        String finalFileName,
        String checksumAlgorithm,
        int streamBufferSize
) {
}
