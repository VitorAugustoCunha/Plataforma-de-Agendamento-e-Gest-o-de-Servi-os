package com.agenda.plataform.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.agenda.plataform.entity.ProviderProfileEntity;

@Repository
public interface ProviderProfileRepository extends JpaRepository<ProviderProfileEntity, UUID>, JpaSpecificationExecutor<ProviderProfileEntity> {
    
    Optional<ProviderProfileEntity> findByUserId(UUID userId);
}
