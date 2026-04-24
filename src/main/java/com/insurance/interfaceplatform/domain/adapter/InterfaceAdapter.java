package com.insurance.interfaceplatform.domain.adapter;

import com.insurance.interfaceplatform.domain.common.ProtocolType;

public interface InterfaceAdapter {

    boolean supports(ProtocolType protocolType);

    AdapterExecutionResult execute(AdapterExecutionCommand command);
}
