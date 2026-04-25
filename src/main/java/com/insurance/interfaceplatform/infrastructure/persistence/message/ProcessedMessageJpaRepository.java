package com.insurance.interfaceplatform.infrastructure.persistence.message;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessedMessageJpaRepository extends JpaRepository<ProcessedMessageEntity, Long> {

    Optional<ProcessedMessageEntity> findByTopicNameAndMessageId(String topicName, String messageId);
}
