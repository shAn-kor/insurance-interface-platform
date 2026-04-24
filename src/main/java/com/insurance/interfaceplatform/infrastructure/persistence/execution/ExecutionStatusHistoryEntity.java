package com.insurance.interfaceplatform.infrastructure.persistence.execution;

import com.insurance.interfaceplatform.domain.common.ExecutionStatus;
import com.insurance.interfaceplatform.domain.execution.ExecutionStatusHistory;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "execution_status_history")
public class ExecutionStatusHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "execution_id", nullable = false, length = 80)
    private String executionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 30)
    private ExecutionStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 30)
    private ExecutionStatus toStatus;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected ExecutionStatusHistoryEntity() {
    }

    public ExecutionStatusHistoryEntity(final ExecutionStatusHistory executionStatusHistory) {
        this.id = executionStatusHistory.id();
        this.executionId = executionStatusHistory.executionId();
        this.fromStatus = executionStatusHistory.fromStatus();
        this.toStatus = executionStatusHistory.toStatus();
        this.reason = executionStatusHistory.reason();
        this.changedAt = executionStatusHistory.changedAt();
    }

    public ExecutionStatusHistory toDomain() {
        return new ExecutionStatusHistory(id, executionId, fromStatus, toStatus, reason, changedAt);
    }
}
