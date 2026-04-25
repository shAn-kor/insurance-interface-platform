package com.insurance.interfaceplatform.infrastructure.persistence.message;

import com.insurance.interfaceplatform.domain.message.ProcessedMessage;
import com.insurance.interfaceplatform.domain.message.ProcessedMessageRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProcessedMessageJpaAdapter implements ProcessedMessageRepository {

    private final ProcessedMessageJpaRepository processedMessageJpaRepository;

    @Override
    public ProcessedMessage save(final ProcessedMessage processedMessage) {
        return processedMessageJpaRepository.saveAndFlush(new ProcessedMessageEntity(processedMessage)).toDomain();
    }

    @Override
    public Optional<ProcessedMessage> findByTopicNameAndMessageId(final String topicName, final String messageId) {
        return processedMessageJpaRepository.findByTopicNameAndMessageId(topicName, messageId)
                .map(ProcessedMessageEntity::toDomain);
    }
}
