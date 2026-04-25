package com.insurance.interfaceplatform.infrastructure.persistence.batch;

import com.insurance.interfaceplatform.domain.batch.BatchExecutionLock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "batch_execution_lock",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_batch_execution_lock_key", columnNames = "lock_key")
        }
)
public class BatchExecutionLockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "lock_key", nullable = false, length = 240)
    private String lockKey;

    @Column(name = "locked_by", nullable = false, length = 120)
    private String lockedBy;

    @Column(name = "locked_until", nullable = false)
    private Instant lockedUntil;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected BatchExecutionLockEntity() {
    }

    public BatchExecutionLockEntity(final BatchExecutionLock batchExecutionLock) {
        this.id = batchExecutionLock.id();
        this.lockKey = batchExecutionLock.lockKey();
        this.lockedBy = batchExecutionLock.lockedBy();
        this.lockedUntil = batchExecutionLock.lockedUntil();
        this.createdAt = batchExecutionLock.createdAt();
        this.updatedAt = batchExecutionLock.updatedAt();
    }

    public BatchExecutionLock toDomain() {
        return new BatchExecutionLock(id, lockKey, lockedBy, lockedUntil, createdAt, updatedAt);
    }
}
