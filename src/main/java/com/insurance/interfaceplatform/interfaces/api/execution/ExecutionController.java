package com.insurance.interfaceplatform.interfaces.api.execution;

import com.insurance.interfaceplatform.application.execution.ExecutionApplicationService;
import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExecutionController {

    private final ExecutionApplicationService executionApplicationService;

    public ExecutionController(final ExecutionApplicationService executionApplicationService) {
        this.executionApplicationService = executionApplicationService;
    }

    @PostMapping("/interfaces/{interfaceId}/executions")
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ExecutionResponse> create(
            @PathVariable final Long interfaceId,
            @Valid @RequestBody final ExecutionDto.CreateRequest request
    ) {
        return ApiResponse.success(ExecutionResponse.from(executionApplicationService.create(request.toCommand(interfaceId))));
    }

    @GetMapping("/executions/{executionId}")
    public ApiResponse<ExecutionResponse> get(@PathVariable final String executionId) {
        return ApiResponse.success(ExecutionResponse.from(executionApplicationService.get(executionId)));
    }

    @GetMapping("/executions/{executionId}/status")
    public ApiResponse<ExecutionStatus> getStatus(@PathVariable final String executionId) {
        return ApiResponse.success(executionApplicationService.get(executionId).status());
    }

    @PatchMapping("/executions/{executionId}/status")
    public ApiResponse<ExecutionResponse> changeStatus(
            @PathVariable final String executionId,
            @RequestParam final ExecutionStatus status,
            @RequestParam(defaultValue = "상태 변경") final String reason
    ) {
        return ApiResponse.success(ExecutionResponse.from(executionApplicationService.changeStatus(executionId, status, reason)));
    }
}
