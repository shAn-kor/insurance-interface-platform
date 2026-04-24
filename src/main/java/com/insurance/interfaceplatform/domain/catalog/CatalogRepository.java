package com.insurance.interfaceplatform.domain.catalog;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CatalogRepository {

    InterfaceDefinition save(InterfaceDefinition interfaceDefinition);

    Optional<InterfaceDefinition> findById(Long id);

    Page<InterfaceDefinition> findAll(Pageable pageable);
}
