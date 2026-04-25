package com.insurance.interfaceplatform.domain.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExecutionRunRepository {

    ExecutionRun save(ExecutionRun executionRun);

    Optional<ExecutionRun> findByExecutionId(String executionId);

    Page<ExecutionRun> findAll(Pageable pageable);

    int updateIfStatus(ExecutionRun executionRun, ExecutionStatus expectedStatus);
}
