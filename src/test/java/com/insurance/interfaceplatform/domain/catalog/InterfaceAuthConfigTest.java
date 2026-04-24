package com.insurance.interfaceplatform.domain.catalog;

import com.insurance.interfaceplatform.domain.common.ProtocolType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InterfaceAuthConfigTest {

    @Test
    @DisplayName("toString_민감인증Alias_마스킹한다")
    void toString_sensitiveAuthAliases_masksValues() {
        final InterfaceAuthConfig authConfig = new InterfaceAuthConfig("API_KEY", "real-api-key", "real-token", "user", "secret-password", "cert");

        final String result = authConfig.toString();

        assertThat(result).doesNotContain("real-api-key");
        assertThat(result).doesNotContain("real-token");
        assertThat(result).doesNotContain("secret-password");
        assertThat(result).contains("***");
    }

    @Test
    @DisplayName("register_정상명령_프로토콜과엔드포인트를보존한다")
    void register_validCommand_keepsProtocolAndEndpoint() {
        final InterfaceDefinition result = InterfaceDefinition.register(new RegisterInterfaceCommand(
                "IF-FSS-CONTRACT-001",
                "금감원 계약 통계 송신",
                "CONTRACT",
                "FSS",
                ProtocolType.REST,
                "description",
                new InterfaceEndpointCommand("https://external.example.com/contracts", "POST", null, null, null, null, 3000),
                null,
                null
        ));

        assertThat(result.protocolType()).isEqualTo(ProtocolType.REST);
        assertThat(result.endpoint().endpointUrl()).isEqualTo("https://external.example.com/contracts");
    }
}
