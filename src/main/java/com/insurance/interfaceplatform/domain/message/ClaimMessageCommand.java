package com.insurance.interfaceplatform.domain.message;

public record ClaimMessageCommand(
        String messageId,
        String topicName,
        Integer partitionNo,
        Long offsetNo,
        String idempotencyKey,
        String executionId
) {
}
