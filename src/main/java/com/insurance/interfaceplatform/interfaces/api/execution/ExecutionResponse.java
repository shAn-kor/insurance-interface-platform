package com.insurance.interfaceplatform.interfaces.api.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import java.time.Instant;

public record ExecutionResponse(
        String executionId,
        Long interfaceId,
        ExecutionType executionType,
        ProtocolType protocolType,
        ExecutionStatus status,
        Instant requestedAt,
        Instant startedAt,
        Instant endedAt,
        Long durationMillis
) {

    public static ExecutionResponse from(final ExecutionRun executionRun) {
        return new ExecutionResponse(
                executionRun.executionId(),
                executionRun.interfaceId(),
                executionRun.executionType(),
                executionRun.protocolType(),
                executionRun.status(),
                executionRun.requestedAt(),
                executionRun.startedAt(),
                executionRun.endedAt(),
                executionRun.durationMillis()
        );
    }
}
