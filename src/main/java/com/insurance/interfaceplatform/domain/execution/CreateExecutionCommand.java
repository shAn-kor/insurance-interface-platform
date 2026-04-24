package com.insurance.interfaceplatform.domain.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;

public record CreateExecutionCommand(
        Long interfaceId,
        ExecutionType executionType,
        ProtocolType protocolType,
        String idempotencyKey,
        String requestPayloadHash,
        String payloadSummary
) {
}
