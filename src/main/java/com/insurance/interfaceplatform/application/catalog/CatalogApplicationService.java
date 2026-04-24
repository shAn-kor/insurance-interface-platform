package com.insurance.interfaceplatform.application.catalog;

import com.insurance.interfaceplatform.domain.catalog.CatalogRepository;
import com.insurance.interfaceplatform.domain.catalog.InterfaceDefinition;
import com.insurance.interfaceplatform.domain.catalog.RegisterInterfaceCommand;
import com.insurance.interfaceplatform.support.error.CoreException;
import com.insurance.interfaceplatform.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogApplicationService {

    private final CatalogRepository catalogRepository;

    public CatalogApplicationService(final CatalogRepository catalogRepository) {
        this.catalogRepository = catalogRepository;
    }

    @Transactional
    public InterfaceDefinition register(final RegisterInterfaceCommand command) {
        final InterfaceDefinition interfaceDefinition = InterfaceDefinition.register(command);
        return catalogRepository.save(interfaceDefinition);
    }

    @Transactional(readOnly = true)
    public InterfaceDefinition get(final Long interfaceId) {
        return catalogRepository.findById(interfaceId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "인터페이스를 찾을 수 없습니다."));
    }

    @Transactional(readOnly = true)
    public Page<InterfaceDefinition> list(final Pageable pageable) {
        return catalogRepository.findAll(pageable);
    }

    @Transactional
    public InterfaceDefinition update(final Long interfaceId, final RegisterInterfaceCommand command) {
        final InterfaceDefinition found = catalogRepository.findById(interfaceId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "인터페이스를 찾을 수 없습니다."));
        final InterfaceDefinition updated = found.update(command);
        return catalogRepository.save(updated);
    }

    @Transactional
    public InterfaceDefinition enable(final Long interfaceId) {
        final InterfaceDefinition found = catalogRepository.findById(interfaceId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "인터페이스를 찾을 수 없습니다."));
        return catalogRepository.save(found.enable());
    }

    @Transactional
    public InterfaceDefinition disable(final Long interfaceId) {
        final InterfaceDefinition found = catalogRepository.findById(interfaceId)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "인터페이스를 찾을 수 없습니다."));
        return catalogRepository.save(found.disable());
    }
}
