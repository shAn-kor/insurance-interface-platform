package com.insurance.interfaceplatform.domain.adapter;

import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.domain.catalog.InterfaceEndpoint;

public record AdapterExecutionCommand(
        Long interfaceId,
        String executionId,
        ProtocolType protocolType,
        InterfaceEndpoint endpoint,
        String payloadSummary,
        String idempotencyKey
) {
}
