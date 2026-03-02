package com.agenda.plataform.entity;

import java.time.OffsetDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "provider_profile")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProviderProfileEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private UserEntity user;

    @Column(length = 1000)
    private String bio;

    @Column(name = "location_text", length = 200)
    private String locationText;

    @Column(name = "min_advance_minutes")
    private Integer minAdvanceMinutes;

    @Column(name = "cancel_window_minutes")
    private Integer cancelWindowMinutes;

    @Column(name = "slot_step_minutes")
    private Integer slotStepMinutes;

    @Column(nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(nullable = false)
    private OffsetDateTime updatedAt;
}
