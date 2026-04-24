package com.insurance.interfaceplatform.domain.adapter;

import com.insurance.interfaceplatform.domain.common.FailureType;
import com.insurance.interfaceplatform.domain.common.FallbackType;

public record AdapterExecutionResult(
        boolean success,
        FailureType failureType,
        String responseSummary,
        long durationMillis,
        boolean fallbackApplied,
        FallbackType fallbackType
) {

    public static AdapterExecutionResult success(final String responseSummary, final long durationMillis) {
        return new AdapterExecutionResult(true, null, responseSummary, durationMillis, false, FallbackType.NONE);
    }

    public static AdapterExecutionResult failed(
            final FailureType failureType,
            final String responseSummary,
            final long durationMillis,
            final FallbackType fallbackType
    ) {
        return new AdapterExecutionResult(false, failureType, responseSummary, durationMillis,
                fallbackType != FallbackType.NONE, fallbackType);
    }
}
