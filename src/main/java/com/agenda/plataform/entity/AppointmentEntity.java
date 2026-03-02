package com.agenda.plataform.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import com.agenda.plataform.enums.AppointmentStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "appointment",
    indexes = {
        @Index(name = "idx_appointment_provider_start", columnList = "provider_id,start_at"),
        @Index(name = "idx_appointment_client_created", columnList = "client_id,created_at")
    }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppointmentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private UserEntity client;

    @ManyToOne
    @JoinColumn(name = "provider_id", nullable = false)
    private UserEntity provider;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private ServiceOfferingEntity service;

    @Column(name = "start_at", nullable = false)
    private OffsetDateTime startAt;

    @Column(name = "end_at", nullable = false)
    private OffsetDateTime endAt;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    @Column(name = "cancel_reason", length = 255)
    private String cancelReason;

    @Column(name = "canceled_at")
    private OffsetDateTime canceledAt;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;
}
