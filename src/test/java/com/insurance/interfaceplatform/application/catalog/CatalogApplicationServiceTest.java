package com.insurance.interfaceplatform.application.catalog;

import com.insurance.interfaceplatform.domain.catalog.CatalogRepository;
import com.insurance.interfaceplatform.domain.catalog.InterfaceDefinition;
import com.insurance.interfaceplatform.domain.catalog.InterfaceEndpointCommand;
import com.insurance.interfaceplatform.domain.catalog.RegisterInterfaceCommand;
import com.insurance.interfaceplatform.domain.catalog.ResiliencePolicyCommand;
import com.insurance.interfaceplatform.domain.common.BackoffType;
import com.insurance.interfaceplatform.domain.common.InterfaceStatus;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogApplicationServiceTest {

    @Mock
    private CatalogRepository catalogRepository;

    @InjectMocks
    private CatalogApplicationService catalogApplicationService;

    @Test
    @DisplayName("register_정상요청_활성상태인터페이스를저장한다")
    void register_validCommand_savesActiveInterface() {
        final RegisterInterfaceCommand command = command();
        when(catalogRepository.save(any(InterfaceDefinition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final InterfaceDefinition result = catalogApplicationService.register(command);

        assertThat(result.interfaceCode()).isEqualTo("IF-FSS-CONTRACT-001");
        assertThat(result.status()).isEqualTo(InterfaceStatus.ACTIVE);
        assertThat(result.resiliencePolicy().maxRetryCount()).isEqualTo(3);
        verify(catalogRepository).save(any(InterfaceDefinition.class));
    }

    @Test
    @DisplayName("disable_존재하는인터페이스_비활성상태로저장한다")
    void disable_existingInterface_savesInactiveStatus() {
        final InterfaceDefinition saved = InterfaceDefinition.register(command());
        when(catalogRepository.findById(1L)).thenReturn(Optional.of(saved));
        when(catalogRepository.save(any(InterfaceDefinition.class))).thenAnswer(invocation -> invocation.getArgument(0));

        final InterfaceDefinition result = catalogApplicationService.disable(1L);

        assertThat(result.status()).isEqualTo(InterfaceStatus.INACTIVE);
    }

    @Test
    @DisplayName("get_없는인터페이스_예외가발생한다")
    void get_missingInterface_throwsException() {
        when(catalogRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> catalogApplicationService.get(1L))
                .hasMessage("인터페이스를 찾을 수 없습니다.");
    }

    private RegisterInterfaceCommand command() {
        return new RegisterInterfaceCommand(
                "IF-FSS-CONTRACT-001",
                "금감원 계약 통계 송신",
                "CONTRACT",
                "FSS",
                ProtocolType.REST,
                "금감원에 일별 계약 통계 데이터를 송신하는 인터페이스",
                new InterfaceEndpointCommand("https://external.example.com/contracts", "POST", null, null, null, null, 3000),
                null,
                new ResiliencePolicyCommand(3000, 3, 1000, BackoffType.FIXED, true, 50, 2000, 10, 30000, false, null, false, null)
        );
    }
}
