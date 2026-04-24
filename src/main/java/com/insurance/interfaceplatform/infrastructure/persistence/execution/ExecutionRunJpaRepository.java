package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionRunJpaRepository extends JpaRepository<ExecutionRunEntity, Long> {

    Optional<ExecutionRunEntity> findByExecutionId(String executionId);
}
