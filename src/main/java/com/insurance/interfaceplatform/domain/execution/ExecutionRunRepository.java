package com.insurance.interfaceplatform.domain.execution;

import java.util.Optional;

public interface ExecutionRunRepository {

    ExecutionRun save(ExecutionRun executionRun);

    Optional<ExecutionRun> findByExecutionId(String executionId);
}
