package com.insurance.interfaceplatform.domain.catalog;

import com.insurance.interfaceplatform.domain.common.BackoffType;

public record ResiliencePolicy(
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

    public static ResiliencePolicy from(final ResiliencePolicyCommand command) {
        if (command == null) {
            return new ResiliencePolicy(3000, 3, 1000, BackoffType.FIXED, true, 50, 2000, 10,
                    30000, false, null, false, null);
        }
        return new ResiliencePolicy(
                command.timeoutMillis(),
                command.maxRetryCount(),
                command.retryIntervalMillis(),
                command.backoffType(),
                command.circuitBreakerEnabled(),
                command.failureRateThreshold(),
                command.slowCallThresholdMillis(),
                command.minimumCallCount(),
                command.openStateWaitMillis(),
                command.bulkheadEnabled(),
                command.maxConcurrentCalls(),
                command.rateLimiterEnabled(),
                command.limitForPeriod()
        );
    }
}
