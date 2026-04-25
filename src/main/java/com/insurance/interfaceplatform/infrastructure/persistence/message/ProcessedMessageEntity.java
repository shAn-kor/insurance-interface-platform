package com.insurance.interfaceplatform.infrastructure.persistence.message;

import com.insurance.interfaceplatform.domain.message.ProcessedMessage;
import com.insurance.interfaceplatform.domain.message.ProcessedMessageStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;

@Entity
@Table(
        name = "processed_message",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_processed_message_topic_message", columnNames = {"topic_name", "message_id"})
        }
)
public class ProcessedMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, length = 120)
    private String messageId;

    @Column(name = "topic_name", nullable = false, length = 120)
    private String topicName;

    @Column(name = "partition_no")
    private Integer partitionNo;

    @Column(name = "offset_no")
    private Long offsetNo;

    @Column(name = "idempotency_key", nullable = false, length = 200)
    private String idempotencyKey;

    @Column(name = "execution_id", length = 80)
    private String executionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private ProcessedMessageStatus status;

    @Column(name = "processed_at")
    private Instant processedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProcessedMessageEntity() {
    }

    public ProcessedMessageEntity(final ProcessedMessage processedMessage) {
        this.id = processedMessage.id();
        this.messageId = processedMessage.messageId();
        this.topicName = processedMessage.topicName();
        this.partitionNo = processedMessage.partitionNo();
        this.offsetNo = processedMessage.offsetNo();
        this.idempotencyKey = processedMessage.idempotencyKey();
        this.executionId = processedMessage.executionId();
        this.status = processedMessage.status();
        this.processedAt = processedMessage.processedAt();
        this.createdAt = processedMessage.createdAt();
        this.updatedAt = processedMessage.updatedAt();
    }

    public ProcessedMessage toDomain() {
        return new ProcessedMessage(id, messageId, topicName, partitionNo, offsetNo, idempotencyKey, executionId,
                status, processedAt, createdAt, updatedAt);
    }
}
