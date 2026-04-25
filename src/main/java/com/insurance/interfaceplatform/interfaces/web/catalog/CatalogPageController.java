package com.insurance.interfaceplatform.interfaces.web.catalog;

import com.insurance.interfaceplatform.application.catalog.CatalogApplicationService;
import com.insurance.interfaceplatform.domain.catalog.InterfaceDefinition;
import com.insurance.interfaceplatform.domain.common.BackoffType;
import com.insurance.interfaceplatform.domain.common.ProtocolType;
import com.insurance.interfaceplatform.interfaces.api.catalog.CatalogDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/interfaces")
public class CatalogPageController {

    private final CatalogApplicationService catalogApplicationService;

    public CatalogPageController(final CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }

    @GetMapping
    public String list(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "10") final int size,
            final Model model
    ) {
        final Pageable pageable = PageRequest.of(page, size);
        final Page<InterfaceDefinition> interfaces = catalogApplicationService.list(pageable);
        model.addAttribute("interfaces", interfaces);
        model.addAttribute("protocolTypes", ProtocolType.values());
        model.addAttribute("backoffTypes", BackoffType.values());
        model.addAttribute("registerRequest", CatalogForm.defaultRequest());
        return "catalog/list";
    }

    @PostMapping
    public String register(
            @ModelAttribute final CatalogForm form,
            final RedirectAttributes redirectAttributes
    ) {
        catalogApplicationService.register(form.toRegisterRequest().toCommand());
        redirectAttributes.addFlashAttribute("message", "인터페이스가 등록되었습니다.");
        return "redirect:/interfaces";
    }

    @PostMapping("/{interfaceId}/enable")
    public String enable(
            @PathVariable final Long interfaceId,
            final RedirectAttributes redirectAttributes
    ) {
        catalogApplicationService.enable(interfaceId);
        redirectAttributes.addFlashAttribute("message", "인터페이스가 활성화되었습니다.");
        return "redirect:/interfaces";
    }

    @PostMapping("/{interfaceId}/disable")
    public String disable(
            @PathVariable final Long interfaceId,
            final RedirectAttributes redirectAttributes
    ) {
        catalogApplicationService.disable(interfaceId);
        redirectAttributes.addFlashAttribute("message", "인터페이스가 비활성화되었습니다.");
        return "redirect:/interfaces";
    }

    @ExceptionHandler(Exception.class)
    public String handleException(
            final Exception exception,
            final RedirectAttributes redirectAttributes
    ) {
        redirectAttributes.addFlashAttribute("error", exception.getMessage());
        return "redirect:/interfaces";
    }

    public record CatalogForm(
            String interfaceCode,
            String interfaceName,
            String businessDomain,
            String externalOrganization,
            ProtocolType protocolType,
            String description,
            String endpointUrl,
            String httpMethod,
            String topicName,
            String queueName,
            String batchJobName,
            String filePath,
            Integer timeoutMillis,
            String authType,
            String apiKeyAlias,
            String tokenAlias,
            String usernameAlias,
            String passwordAlias,
            String certAlias,
            Integer maxRetryCount,
            Integer retryIntervalMillis,
            BackoffType backoffType,
            Boolean circuitBreakerEnabled,
            Integer failureRateThreshold,
            Integer slowCallThresholdMillis,
            Integer minimumCallCount,
            Integer openStateWaitMillis,
            Boolean bulkheadEnabled,
            Integer maxConcurrentCalls,
            Boolean rateLimiterEnabled,
            Integer limitForPeriod
    ) {

        public static CatalogDto.RegisterRequest defaultRequest() {
            return new CatalogDto.RegisterRequest(
                    "IF-FSS-CONTRACT-001",
                    "금감원 계약 통계 송신",
                    "CONTRACT",
                    "FSS",
                    ProtocolType.REST,
                    "금감원에 일별 계약 통계 데이터를 송신하는 인터페이스",
                    new CatalogDto.EndpointRequest("https://external.example.com/contracts/statistics", "POST", null, null, null, null, 3000),
                    new CatalogDto.AuthConfigRequest("API_KEY", "FSS_API_KEY", null, null, null, null),
                    new CatalogDto.ResiliencePolicyRequest(3000, 3, 1000, BackoffType.FIXED, true, 50, 2000, 10,
                            30000, true, 20, false, 0)
            );
        }

        public CatalogDto.RegisterRequest toRegisterRequest() {
            return new CatalogDto.RegisterRequest(
                    interfaceCode,
                    interfaceName,
                    businessDomain,
                    externalOrganization,
                    protocolType,
                    description,
                    new CatalogDto.EndpointRequest(endpointUrl, httpMethod, topicName, queueName, batchJobName, filePath, timeoutMillis),
                    new CatalogDto.AuthConfigRequest(authType, apiKeyAlias, tokenAlias, usernameAlias, passwordAlias, certAlias),
                    new CatalogDto.ResiliencePolicyRequest(timeoutMillis, maxRetryCount, retryIntervalMillis, backoffType,
                            circuitBreakerEnabled, failureRateThreshold, slowCallThresholdMillis, minimumCallCount,
                            openStateWaitMillis, bulkheadEnabled, maxConcurrentCalls, rateLimiterEnabled, limitForPeriod)
            );
        }
    }
}
