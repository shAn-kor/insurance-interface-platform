package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExecutionRunJpaRepository extends JpaRepository<ExecutionRunEntity, Long> {

    Optional<ExecutionRunEntity> findByExecutionId(String executionId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update ExecutionRunEntity execution
            set execution.status = :#{#next.status},
                execution.startedAt = :#{#next.startedAt},
                execution.endedAt = :#{#next.endedAt},
                execution.durationMillis = :#{#next.durationMillis},
                execution.success = :#{#next.success},
                execution.failureType = :#{#next.failureType},
                execution.failureMessage = :#{#next.failureMessage},
                execution.retryable = :#{#next.retryable},
                execution.retryCount = :#{#next.retryCount},
                execution.attemptCount = :#{#next.attemptCount},
                execution.maxAttemptCount = :#{#next.maxAttemptCount},
                execution.nextRetryAt = :#{#next.nextRetryAt},
                execution.fallbackApplied = :#{#next.fallbackApplied},
                execution.fallbackType = :#{#next.fallbackType},
                execution.circuitBreakerState = :#{#next.circuitBreakerState},
                execution.lastAttemptAt = :#{#next.lastAttemptAt}
            where execution.executionId = :executionId
              and execution.status = :expectedStatus
            """)
    int updateIfStatus(
            @Param("executionId") String executionId,
            @Param("expectedStatus") ExecutionStatus expectedStatus,
            @Param("next") ExecutionRunEntity next
    );
}
