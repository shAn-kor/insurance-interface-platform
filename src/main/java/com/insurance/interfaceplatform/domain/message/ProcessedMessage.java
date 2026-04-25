package com.insurance.interfaceplatform.domain.message;

import java.time.Instant;

public record ProcessedMessage(
        Long id,
        String messageId,
        String topicName,
        Integer partitionNo,
        Long offsetNo,
        String idempotencyKey,
        String executionId,
        ProcessedMessageStatus status,
        Instant processedAt,
        Instant createdAt,
        Instant updatedAt
) {
    public static ProcessedMessage claim(final ClaimMessageCommand command, final Instant now) {
        return new ProcessedMessage(
                null,
                command.messageId(),
                command.topicName(),
                command.partitionNo(),
                command.offsetNo(),
                command.idempotencyKey(),
                command.executionId(),
                ProcessedMessageStatus.PROCESSING,
                null,
                now,
                now
        );
    }

    public ProcessedMessage processed(final String nextExecutionId, final Instant now) {
        return new ProcessedMessage(id, messageId, topicName, partitionNo, offsetNo, idempotencyKey, nextExecutionId,
                ProcessedMessageStatus.PROCESSED, now, createdAt, now);
    }

    public ProcessedMessage failed(final Instant now) {
        return new ProcessedMessage(id, messageId, topicName, partitionNo, offsetNo, idempotencyKey, executionId,
                ProcessedMessageStatus.FAILED, processedAt, createdAt, now);
    }
}
