package com.insurance.interfaceplatform.support.response;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorBody error,
        Instant timestamp
) {

    public static <T> ApiResponse<T> success(final T data) {
        return new ApiResponse<>(true, data, null, Instant.now());
    }

    public static ApiResponse<Void> empty() {
        return new ApiResponse<>(true, null, null, Instant.now());
    }

    public static ApiResponse<Void> fail(final String code, final String message) {
        return new ApiResponse<>(false, null, new ErrorBody(code, message), Instant.now());
    }

    public record ErrorBody(String code, String message) {
    }
}
