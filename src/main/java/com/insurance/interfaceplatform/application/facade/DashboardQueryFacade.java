package com.insurance.interfaceplatform.application.facade;

import com.insurance.interfaceplatform.application.catalog.CatalogApplicationService;
import com.insurance.interfaceplatform.application.execution.ExecutionApplicationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class DashboardQueryFacade {

    private final CatalogApplicationService catalogApplicationService;
    private final ExecutionApplicationService executionApplicationService;

    public DashboardQueryFacade(
            final CatalogApplicationService catalogApplicationService,
            final ExecutionApplicationService executionApplicationService
    ) {
        this.catalogApplicationService = catalogApplicationService;
        this.executionApplicationService = executionApplicationService;
    }

    public DashboardSummary getSummary() {
        return new DashboardSummary(
                catalogApplicationService.list(PageRequest.of(0, 1)).getTotalElements(),
                executionApplicationService.list(PageRequest.of(0, 1)).getTotalElements()
        );
    }

    public record DashboardSummary(
            long interfaceCount,
            long executionCount
    ) {
    }
}
