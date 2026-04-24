package com.insurance.interfaceplatform.interfaces.api.catalog;

import com.insurance.interfaceplatform.domain.catalog.InterfaceDefinition;
import com.insurance.interfaceplatform.domain.common.InterfaceStatus;
import com.insurance.interfaceplatform.domain.common.ProtocolType;

public record InterfaceResponse(
        Long id,
        String interfaceCode,
        String interfaceName,
        String businessDomain,
        String externalOrganization,
        ProtocolType protocolType,
        String description,
        InterfaceStatus status
) {

    public static InterfaceResponse from(final InterfaceDefinition interfaceDefinition) {
        return new InterfaceResponse(
                interfaceDefinition.id(),
                interfaceDefinition.interfaceCode(),
                interfaceDefinition.interfaceName(),
                interfaceDefinition.businessDomain(),
                interfaceDefinition.externalOrganization(),
                interfaceDefinition.protocolType(),
                interfaceDefinition.description(),
                interfaceDefinition.status()
        );
    }
}
