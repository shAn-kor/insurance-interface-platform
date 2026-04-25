package com.insurance.interfaceplatform.application.message;

import com.insurance.interfaceplatform.domain.message.ClaimMessageCommand;
import com.insurance.interfaceplatform.domain.message.MessageClaimResult;
import com.insurance.interfaceplatform.domain.message.ProcessedMessage;
import com.insurance.interfaceplatform.domain.message.ProcessedMessageRepository;
import java.time.Clock;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

public class InMemoryProcessedMessageRepository implements ProcessedMessageRepository {

    private final Map<String, ProcessedMessage> store = new HashMap<>();
    private long sequence = 1L;

    @Override
    public ProcessedMessage save(final ProcessedMessage processedMessage) {
        final String key = key(processedMessage.topicName(), processedMessage.messageId());
        final ProcessedMessage existing = store.get(key);
        if (processedMessage.id() == null && existing != null) {
            throw new DataIntegrityViolationException("duplicated message");
        }
        final ProcessedMessage saved = new ProcessedMessage(
                processedMessage.id() == null ? sequence++ : processedMessage.id(),
                processedMessage.messageId(),
                processedMessage.topicName(),
                processedMessage.partitionNo(),
                processedMessage.offsetNo(),
                processedMessage.idempotencyKey(),
                processedMessage.executionId(),
                processedMessage.status(),
                processedMessage.processedAt(),
                processedMessage.createdAt(),
                processedMessage.updatedAt()
        );
        store.put(key, saved);
        return saved;
    }

    @Override
    public Optional<ProcessedMessage> findByTopicNameAndMessageId(final String topicName, final String messageId) {
        return Optional.ofNullable(store.get(key(topicName, messageId)));
    }

    public static String key(final String topicName, final String messageId) {
        return topicName + ":" + messageId;
    }
}
