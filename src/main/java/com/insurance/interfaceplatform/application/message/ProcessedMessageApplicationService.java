package com.insurance.interfaceplatform.application.message;

import com.insurance.interfaceplatform.domain.message.ClaimMessageCommand;
import com.insurance.interfaceplatform.domain.message.MessageClaimResult;
import com.insurance.interfaceplatform.domain.message.ProcessedMessage;
import com.insurance.interfaceplatform.domain.message.ProcessedMessageRepository;
import java.time.Clock;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProcessedMessageApplicationService {

    private final ProcessedMessageRepository processedMessageRepository;
    private final Clock clock;

    @Transactional
    public MessageClaimResult claim(final ClaimMessageCommand command) {
        final Instant now = Instant.now(clock);
        try {
            final ProcessedMessage claimed = processedMessageRepository.save(ProcessedMessage.claim(command, now));
            return MessageClaimResult.claimed(claimed.id());
        } catch (final DataIntegrityViolationException exception) {
            return MessageClaimResult.duplicated();
        }
    }

    @Transactional
    public void markProcessed(final String topicName, final String messageId, final String executionId) {
        final ProcessedMessage processedMessage = processedMessageRepository.findByTopicNameAndMessageId(topicName, messageId)
                .orElseThrow();
        processedMessageRepository.save(processedMessage.processed(executionId, Instant.now(clock)));
    }

    @Transactional
    public void markFailed(final String topicName, final String messageId) {
        final ProcessedMessage processedMessage = processedMessageRepository.findByTopicNameAndMessageId(topicName, messageId)
                .orElseThrow();
        processedMessageRepository.save(processedMessage.failed(Instant.now(clock)));
    }
}
