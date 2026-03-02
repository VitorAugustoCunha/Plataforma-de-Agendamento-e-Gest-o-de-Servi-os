package com.agenda.plataform.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.agenda.plataform.entity.ProviderTimeOffEntity;

@Repository
public interface ProviderTimeOffRepository extends JpaRepository<ProviderTimeOffEntity, UUID>, JpaSpecificationExecutor<ProviderTimeOffEntity> {
}
