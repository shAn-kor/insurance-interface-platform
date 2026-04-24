package com.insurance.interfaceplatform.domain.catalog;

import com.insurance.interfaceplatform.domain.common.InterfaceStatus;
import com.insurance.interfaceplatform.domain.common.ProtocolType;

public record InterfaceDefinition(
        Long id,
        String interfaceCode,
        String interfaceName,
        String businessDomain,
        String externalOrganization,
        ProtocolType protocolType,
        String description,
        InterfaceStatus status,
        InterfaceEndpoint endpoint,
        InterfaceAuthConfig authConfig,
        ResiliencePolicy resiliencePolicy
) {

    public static InterfaceDefinition register(final RegisterInterfaceCommand command) {
        return new InterfaceDefinition(
                null,
                command.interfaceCode(),
                command.interfaceName(),
                command.businessDomain(),
                command.externalOrganization(),
                command.protocolType(),
                command.description(),
                InterfaceStatus.ACTIVE,
                InterfaceEndpoint.from(command.endpoint()),
                InterfaceAuthConfig.from(command.authConfig()),
                ResiliencePolicy.from(command.resiliencePolicy())
        );
    }

    public InterfaceDefinition update(final RegisterInterfaceCommand command) {
        return new InterfaceDefinition(
                id,
                interfaceCode,
                command.interfaceName(),
                command.businessDomain(),
                command.externalOrganization(),
                command.protocolType(),
                command.description(),
                status,
                InterfaceEndpoint.from(command.endpoint()),
                InterfaceAuthConfig.from(command.authConfig()),
                ResiliencePolicy.from(command.resiliencePolicy())
        );
    }

    public InterfaceDefinition enable() {
        return new InterfaceDefinition(id, interfaceCode, interfaceName, businessDomain, externalOrganization, protocolType,
                description, InterfaceStatus.ACTIVE, endpoint, authConfig, resiliencePolicy);
    }

    public InterfaceDefinition disable() {
        return new InterfaceDefinition(id, interfaceCode, interfaceName, businessDomain, externalOrganization, protocolType,
                description, InterfaceStatus.INACTIVE, endpoint, authConfig, resiliencePolicy);
    }
}
