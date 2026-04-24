package com.insurance.interfaceplatform.domain.catalog;

import com.insurance.interfaceplatform.domain.common.ProtocolType;

public record RegisterInterfaceCommand(
        String interfaceCode,
        String interfaceName,
        String businessDomain,
        String externalOrganization,
        ProtocolType protocolType,
        String description,
        InterfaceEndpointCommand endpoint,
        InterfaceAuthConfigCommand authConfig,
        ResiliencePolicyCommand resiliencePolicy
) {
}
