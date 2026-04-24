package com.insurance.interfaceplatform.interfaces.api.catalog;

import com.insurance.interfaceplatform.application.catalog.CatalogApplicationService;
import com.insurance.interfaceplatform.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/interfaces")
public class CatalogController {

    private final CatalogApplicationService catalogApplicationService;

    public CatalogController(final CatalogApplicationService catalogApplicationService) {
        this.catalogApplicationService = catalogApplicationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<InterfaceResponse> register(@Valid @RequestBody final CatalogDto.RegisterRequest request) {
        return ApiResponse.success(InterfaceResponse.from(catalogApplicationService.register(request.toCommand())));
    }

    @GetMapping
    public ApiResponse<Page<InterfaceResponse>> list(final Pageable pageable) {
        return ApiResponse.success(catalogApplicationService.list(pageable).map(InterfaceResponse::from));
    }

    @GetMapping("/{interfaceId}")
    public ApiResponse<InterfaceResponse> get(@PathVariable final Long interfaceId) {
        return ApiResponse.success(InterfaceResponse.from(catalogApplicationService.get(interfaceId)));
    }

    @PutMapping("/{interfaceId}")
    public ApiResponse<InterfaceResponse> update(
            @PathVariable final Long interfaceId,
            @Valid @RequestBody final CatalogDto.RegisterRequest request
    ) {
        return ApiResponse.success(InterfaceResponse.from(catalogApplicationService.update(interfaceId, request.toCommand())));
    }

    @PatchMapping("/{interfaceId}/enable")
    public ApiResponse<InterfaceResponse> enable(@PathVariable final Long interfaceId) {
        return ApiResponse.success(InterfaceResponse.from(catalogApplicationService.enable(interfaceId)));
    }

    @PatchMapping("/{interfaceId}/disable")
    public ApiResponse<InterfaceResponse> disable(@PathVariable final Long interfaceId) {
        return ApiResponse.success(InterfaceResponse.from(catalogApplicationService.disable(interfaceId)));
    }
}
