package com.insurance.interfaceplatform.domain.catalog;

public record InterfaceAuthConfigCommand(
        String authType,
        String apiKeyAlias,
        String tokenAlias,
        String usernameAlias,
        String passwordAlias,
        String certAlias
) {
    @Override
    public String toString() {
        return "InterfaceAuthConfigCommand[authType=" + authType
                + ", apiKeyAlias=***"
                + ", tokenAlias=***"
                + ", usernameAlias=" + usernameAlias
                + ", passwordAlias=***"
                + ", certAlias=***]";
    }
}
