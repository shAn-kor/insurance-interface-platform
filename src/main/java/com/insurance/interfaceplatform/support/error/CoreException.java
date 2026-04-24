package com.insurance.interfaceplatform.support.error;

public class CoreException extends RuntimeException {

    private final ErrorType errorType;

    public CoreException(final ErrorType errorType) {
        super(errorType.message());
        this.errorType = errorType;
    }

    public CoreException(final ErrorType errorType, final String message) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType errorType() {
        return errorType;
    }
}
