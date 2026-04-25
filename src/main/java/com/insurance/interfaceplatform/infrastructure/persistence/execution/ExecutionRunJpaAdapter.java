package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.execution.ExecutionRun;
import com.insurance.interfaceplatform.domain.execution.ExecutionRunRepository;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public Page<ExecutionRun> findAll(final Pageable pageable) {
        return executionRunJpaRepository.findAll(pageable)
                .map(ExecutionRunEntity::toDomain);
    }

    @Override
    public int updateIfStatus(final ExecutionRun executionRun, final ExecutionStatus expectedStatus) {
        return executionRunJpaRepository.updateIfStatus(executionRun.executionId(), expectedStatus, new ExecutionRunEntity(executionRun));
    }
}
