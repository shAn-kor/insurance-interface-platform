package com.insurance.interfaceplatform.infrastructure.persistence.batch;

import java.time.Instant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BatchExecutionLockJpaRepository extends JpaRepository<BatchExecutionLockEntity, Long> {

    Optional<BatchExecutionLockEntity> findByLockKey(String lockKey);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            update BatchExecutionLockEntity lock
            set lock.lockedBy = :lockedBy,
                lock.lockedUntil = :lockedUntil,
                lock.updatedAt = :now
            where lock.lockKey = :lockKey
              and lock.lockedUntil < :now
            """)
    int acquireExpired(
            @Param("lockKey") String lockKey,
            @Param("lockedBy") String lockedBy,
            @Param("lockedUntil") Instant lockedUntil,
            @Param("now") Instant now
    );

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("""
            delete from BatchExecutionLockEntity lock
            where lock.lockKey = :lockKey
              and lock.lockedBy = :lockedBy
            """)
    int release(@Param("lockKey") String lockKey, @Param("lockedBy") String lockedBy);
}
