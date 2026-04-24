package com.insurance.interfaceplatform.interfaces.api.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.domain.execution.CreateExecutionCommand;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class ExecutionDto {

    private ExecutionDto() {
    }

    public record CreateRequest(
            @NotNull ExecutionType executionType,
            @NotNull ProtocolType protocolType,
            @NotBlank String idempotencyKey,
            String requestPayloadHash,
            String payloadSummary
    ) {
        public CreateExecutionCommand toCommand(final Long interfaceId) {
            return new CreateExecutionCommand(
                    interfaceId,
                    executionType,
                    protocolType,
                    idempotencyKey,
                    requestPayloadHash,
                    payloadSummary
            );
        }
    }
}
