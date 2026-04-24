package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistory;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistoryRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ExecutionStatusHistoryJpaAdapter implements ExecutionStatusHistoryRepository {

    private final ExecutionStatusHistoryJpaRepository executionStatusHistoryJpaRepository;

    public ExecutionStatusHistoryJpaAdapter(final ExecutionStatusHistoryJpaRepository executionStatusHistoryJpaRepository) {
        this.executionStatusHistoryJpaRepository = executionStatusHistoryJpaRepository;
    }

    @Override
    public ExecutionStatusHistory save(final ExecutionStatusHistory executionStatusHistory) {
        return executionStatusHistoryJpaRepository.save(new ExecutionStatusHistoryEntity(executionStatusHistory)).toDomain();
    }
}
