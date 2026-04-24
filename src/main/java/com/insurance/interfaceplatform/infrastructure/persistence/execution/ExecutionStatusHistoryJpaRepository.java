package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionStatusHistoryJpaRepository extends JpaRepository<ExecutionStatusHistoryEntity, Long> {
}
