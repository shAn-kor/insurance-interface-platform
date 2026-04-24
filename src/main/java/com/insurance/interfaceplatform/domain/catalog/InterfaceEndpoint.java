package com.insurance.interfaceplatform.domain.catalog;

public record InterfaceEndpoint(
        String endpointUrl,
        String httpMethod,
        String topicName,
        String queueName,
        String batchJobName,
        String filePath,
        Integer timeoutMillis
) {

    public static InterfaceEndpoint from(final InterfaceEndpointCommand command) {
        if (command == null) {
            return new InterfaceEndpoint(null, null, null, null, null, null, null);
        }
        return new InterfaceEndpoint(
                command.endpointUrl(),
                command.httpMethod(),
                command.topicName(),
                command.queueName(),
                command.batchJobName(),
                command.filePath(),
                command.timeoutMillis()
        );
    }
}
