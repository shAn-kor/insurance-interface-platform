package com.insurance.interfaceplatform.interfaces.api.catalog;

import com.insurance.interfaceplatform.domain.catalog.InterfaceAuthConfigCommand;
import com.insurance.interfaceplatform.domain.catalog.InterfaceEndpointCommand;
import com.insurance.interfaceplatform.domain.catalog.RegisterInterfaceCommand;
import com.insurance.interfaceplatform.domain.catalog.ResiliencePolicyCommand;
import com.insurance.interfaceplatform.domain.common.BackoffType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public final class CatalogDto {

    private CatalogDto() {
    }

    public record RegisterRequest(
            @NotBlank String interfaceCode,
            @NotBlank String interfaceName,
            @NotBlank String businessDomain,
            @NotBlank String externalOrganization,
            @NotNull ProtocolType protocolType,
            String description,
            @Valid EndpointRequest endpoint,
            @Valid AuthConfigRequest authConfig,
            @Valid ResiliencePolicyRequest resiliencePolicy
    ) {
        public RegisterInterfaceCommand toCommand() {
            return new RegisterInterfaceCommand(
                    interfaceCode,
                    interfaceName,
                    businessDomain,
                    externalOrganization,
                    protocolType,
                    description,
                    endpoint == null ? null : endpoint.toCommand(),
                    authConfig == null ? null : authConfig.toCommand(),
                    resiliencePolicy == null ? null : resiliencePolicy.toCommand()
            );
        }
    }

    public record EndpointRequest(
            String endpointUrl,
            String httpMethod,
            String topicName,
            String queueName,
            String batchJobName,
            String filePath,
            Integer timeoutMillis
    ) {
        public InterfaceEndpointCommand toCommand() {
            return new InterfaceEndpointCommand(endpointUrl, httpMethod, topicName, queueName, batchJobName, filePath, timeoutMillis);
        }
    }

    public record AuthConfigRequest(
            String authType,
            String apiKeyAlias,
            String tokenAlias,
            String usernameAlias,
            String passwordAlias,
            String certAlias
    ) {
        public InterfaceAuthConfigCommand toCommand() {
            return new InterfaceAuthConfigCommand(authType, apiKeyAlias, tokenAlias, usernameAlias, passwordAlias, certAlias);
        }

        @Override
        public String toString() {
            return "AuthConfigRequest[authType=" + authType
                    + ", apiKeyAlias=***"
                    + ", tokenAlias=***"
                    + ", usernameAlias=" + usernameAlias
                    + ", passwordAlias=***"
                    + ", certAlias=***]";
        }
    }

    public record ResiliencePolicyRequest(
            Integer timeoutMillis,
            Integer maxRetryCount,
            Integer retryIntervalMillis,
            BackoffType backoffType,
            Boolean circuitBreakerEnabled,
            Integer failureRateThreshold,
            Integer slowCallThresholdMillis,
            Integer minimumCallCount,
            Integer openStateWaitMillis,
            Boolean bulkheadEnabled,
            Integer maxConcurrentCalls,
            Boolean rateLimiterEnabled,
            Integer limitForPeriod
    ) {
        public ResiliencePolicyCommand toCommand() {
            return new ResiliencePolicyCommand(timeoutMillis, maxRetryCount, retryIntervalMillis, backoffType,
                    circuitBreakerEnabled, failureRateThreshold, slowCallThresholdMillis, minimumCallCount,
                    openStateWaitMillis, bulkheadEnabled, maxConcurrentCalls, rateLimiterEnabled, limitForPeriod);
        }
    }
}
