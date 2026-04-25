package com.insurance.interfaceplatform.domain.message;

public record MessageClaimResult(
        boolean claimed,
        Long processedMessageId
) {
    public static MessageClaimResult claimed(final Long processedMessageId) {
        return new MessageClaimResult(true, processedMessageId);
    }

    public static MessageClaimResult duplicated() {
        return new MessageClaimResult(false, null);
    }
}
