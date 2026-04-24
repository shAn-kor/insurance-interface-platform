package com.insurance.interfaceplatform.domain.catalog;

public record InterfaceAuthConfig(
        String authType,
        String apiKeyAlias,
        String tokenAlias,
        String usernameAlias,
        String passwordAlias,
        String certAlias
) {

    public static InterfaceAuthConfig from(final InterfaceAuthConfigCommand command) {
        if (command == null) {
            return new InterfaceAuthConfig(null, null, null, null, null, null);
        }
        return new InterfaceAuthConfig(
                command.authType(),
                command.apiKeyAlias(),
                command.tokenAlias(),
                command.usernameAlias(),
                command.passwordAlias(),
                command.certAlias()
        );
    }

    @Override
    public String toString() {
        return "InterfaceAuthConfig[authType=" + authType
                + ", apiKeyAlias=***"
                + ", tokenAlias=***"
                + ", usernameAlias=" + usernameAlias
                + ", passwordAlias=***"
                + ", certAlias=***]";
    }
}
