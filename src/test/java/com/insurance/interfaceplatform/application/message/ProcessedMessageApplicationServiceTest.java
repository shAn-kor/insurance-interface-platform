package com.insurance.interfaceplatform.application.message;

import com.insurance.interfaceplatform.domain.message.ClaimMessageCommand;
import com.insurance.interfaceplatform.domain.message.MessageClaimResult;
import com.insurance.interfaceplatform.domain.message.ProcessedMessage;
import com.insurance.interfaceplatform.domain.message.ProcessedMessageStatus;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProcessedMessageApplicationServiceTest {

    private final Clock clock = Clock.fixed(Instant.parse("2026-04-25T10:00:00Z"), ZoneOffset.UTC);

    @Test
    @DisplayName("claim_신규메시지_처리권한을획득한다")
    void claim_newMessage_returnsClaimed() {
        final InMemoryProcessedMessageRepository repository = new InMemoryProcessedMessageRepository();
        final ProcessedMessageApplicationService service = new ProcessedMessageApplicationService(repository, clock);

        final MessageClaimResult result = service.claim(command());

        assertThat(result.claimed()).isTrue();
        assertThat(result.processedMessageId()).isEqualTo(1L);
    }

    @Test
    @DisplayName("claim_중복메시지_중복결과를반환한다")
    void claim_duplicateMessage_returnsDuplicated() {
        final InMemoryProcessedMessageRepository repository = new InMemoryProcessedMessageRepository();
        final ProcessedMessageApplicationService service = new ProcessedMessageApplicationService(repository, clock);
        service.claim(command());

        final MessageClaimResult result = service.claim(command());

        assertThat(result.claimed()).isFalse();
        assertThat(result.processedMessageId()).isNull();
    }

    @Test
    @DisplayName("markProcessed_처리완료_상태와실행ID를갱신한다")
    void markProcessed_existingMessage_updatesStatusAndExecutionId() {
        final InMemoryProcessedMessageRepository repository = new InMemoryProcessedMessageRepository();
        final ProcessedMessageApplicationService service = new ProcessedMessageApplicationService(repository, clock);
        service.claim(command());

        service.markProcessed("interface-execution", "MSG-001", "EXE-001");

        final ProcessedMessage processedMessage = repository.findByTopicNameAndMessageId("interface-execution", "MSG-001").orElseThrow();
        assertThat(processedMessage.status()).isEqualTo(ProcessedMessageStatus.PROCESSED);
        assertThat(processedMessage.executionId()).isEqualTo("EXE-001");
        assertThat(processedMessage.processedAt()).isEqualTo(Instant.now(clock));
    }

    private ClaimMessageCommand command() {
        return new ClaimMessageCommand("MSG-001", "interface-execution", 0, 10L,
                "IF-1:BUSINESS-20260425", null);
    }
}
