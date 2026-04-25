package com.insurance.interfaceplatform.domain.message;

import java.util.Optional;

public interface ProcessedMessageRepository {

    ProcessedMessage save(ProcessedMessage processedMessage);

    Optional<ProcessedMessage> findByTopicNameAndMessageId(String topicName, String messageId);
}
