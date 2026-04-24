package com.insurance.interfaceplatform.domain.catalog;

import com.insurance.interfaceplatform.domain.common.BackoffType;

public record ResiliencePolicyCommand(
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
}
