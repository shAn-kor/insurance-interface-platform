package com.insurance.interfaceplatform.domain.catalog;

public record InterfaceEndpointCommand(
        String endpointUrl,
        String httpMethod,
        String topicName,
        String queueName,
        String batchJobName,
        String filePath,
        Integer timeoutMillis
) {
}
