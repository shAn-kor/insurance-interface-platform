package com.insurance.interfaceplatform.infrastructure.persistence.catalog;

import com.insurance.interfaceplatform.domain.catalog.CatalogRepository;
import com.insurance.interfaceplatform.domain.catalog.InterfaceDefinition;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CatalogJpaRepository implements CatalogRepository {

    private final InterfaceDefinitionJpaRepository interfaceDefinitionJpaRepository;

    public CatalogJpaRepository(final InterfaceDefinitionJpaRepository interfaceDefinitionJpaRepository) {
        this.interfaceDefinitionJpaRepository = interfaceDefinitionJpaRepository;
    }

    @Override
    public InterfaceDefinition save(final InterfaceDefinition interfaceDefinition) {
        final InterfaceDefinitionEntity saved = interfaceDefinitionJpaRepository.save(new InterfaceDefinitionEntity(interfaceDefinition));
        return saved.toDomain();
    }

    @Override
    public Optional<InterfaceDefinition> findById(final Long id) {
        return interfaceDefinitionJpaRepository.findById(id)
                .map(InterfaceDefinitionEntity::toDomain);
    }

    @Override
    public Page<InterfaceDefinition> findAll(final Pageable pageable) {
        return interfaceDefinitionJpaRepository.findAll(pageable)
                .map(InterfaceDefinitionEntity::toDomain);
    }
}
