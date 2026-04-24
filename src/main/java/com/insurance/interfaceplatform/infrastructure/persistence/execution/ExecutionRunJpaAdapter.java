package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import com.insurance.interfaceplatform.domain.execution.ExecutionRunRepository;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class ExecutionRunJpaAdapter implements ExecutionRunRepository {

    private final ExecutionRunJpaRepository executionRunJpaRepository;

    public ExecutionRunJpaAdapter(final ExecutionRunJpaRepository executionRunJpaRepository) {
        this.executionRunJpaRepository = executionRunJpaRepository;
    }

    @Override
    public ExecutionRun save(final ExecutionRun executionRun) {
        return executionRunJpaRepository.save(new ExecutionRunEntity(executionRun)).toDomain();
    }

    @Override
    public Optional<ExecutionRun> findByExecutionId(final String executionId) {
        return executionRunJpaRepository.findByExecutionId(executionId)
                .map(ExecutionRunEntity::toDomain);
    }
}
