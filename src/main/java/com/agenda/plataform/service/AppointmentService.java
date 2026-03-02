package com.agenda.plataform.service;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.entity.ServiceOfferingEntity;
import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.AppointmentStatus;
import com.agenda.plataform.exception.AppointmentConflictException;
import com.agenda.plataform.exception.InvalidBusinessRuleException;
import com.agenda.plataform.exception.ResourceNotFoundException;
import com.agenda.plataform.repository.AppointmentRepository;
import com.agenda.plataform.repository.ProviderTimeOffRepository;
import com.agenda.plataform.util.specification.AppointmentSpecifications;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    
    private final AppointmentRepository appointmentRepository;
    private final ProviderTimeOffRepository providerTimeOffRepository;
    private final UserService userService;
    private final ServiceOfferingService serviceOfferingService;
    
    @Transactional
    public AppointmentEntity create(UUID clientId, UUID serviceId, AppointmentEntity appointmentData) {
        // Validações básicas
        UserEntity client = userService.findById(clientId);
        ServiceOfferingEntity service = serviceOfferingService.findById(serviceId);
        
        if (!service.getActive()) {
            throw new InvalidBusinessRuleException("Serviço não está ativo");
        }
        
        // Validar que startAt < endAt
        if (!appointmentData.getStartAt().isBefore(appointmentData.getEndAt())) {
            throw new InvalidBusinessRuleException("startAt deve ser antes de endAt");
        }
        
        // Provider é determinado pelo serviço
        UserEntity provider = service.getProvider().getUser();
        
        // Validar antecedência mínima
        Integer minAdvance = service.getProvider().getMinAdvanceMinutes();
        if (minAdvance != null) {
            OffsetDateTime minDateTime = OffsetDateTime.now().plusMinutes(minAdvance);
            if (appointmentData.getStartAt().isBefore(minDateTime)) {
                throw new InvalidBusinessRuleException(
                    "Agendamento deve ser feito com antecedência de " + minAdvance + " minutos"
                );
            }
        }
        
        // Verificar conflitos
        checkConflict(provider.getId(), appointmentData.getStartAt(), appointmentData.getEndAt());
        
        // Verificar se está em timeOff
        checkTimeOff(provider.getId(), appointmentData.getStartAt(), appointmentData.getEndAt());
        
        appointmentData.setClient(client);
        appointmentData.setProvider(provider);
        appointmentData.setService(service);
        appointmentData.setStatus(AppointmentStatus.SCHEDULED);
        appointmentData.setCreatedAt(OffsetDateTime.now());
        
        return appointmentRepository.save(appointmentData);
    }
    
    @Transactional(readOnly = true)
    public AppointmentEntity findById(UUID id) {
        return appointmentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Appointment não encontrado com ID: " + id));
    }
    
    @Transactional(readOnly = true)
    public Page<AppointmentEntity> findByClient(UUID clientId, Pageable pageable) {
        Specification<AppointmentEntity> spec = AppointmentSpecifications.byClientId(clientId);
        return appointmentRepository.findAll(spec, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<AppointmentEntity> findByProvider(UUID providerId, Pageable pageable) {
        Specification<AppointmentEntity> spec = AppointmentSpecifications.byProviderId(providerId);
        return appointmentRepository.findAll(spec, pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<AppointmentEntity> findAll(Pageable pageable) {
        return appointmentRepository.findAll(pageable);
    }
    
    @Transactional(readOnly = true)
    public Page<AppointmentEntity> findAll(Specification<AppointmentEntity> spec, Pageable pageable) {
        return appointmentRepository.findAll(spec, pageable);
    }
    
    @Transactional
    public AppointmentEntity cancel(UUID id, String cancelReason) {
        AppointmentEntity appointment = findById(id);
        
        if (appointment.getStatus() == AppointmentStatus.CANCELED) {
            throw new InvalidBusinessRuleException("Agendamento já está cancelado");
        }
        
        if (appointment.getStatus() == AppointmentStatus.COMPLETED) {
            throw new InvalidBusinessRuleException("Não pode cancelar agendamento concluído");
        }
        
        // Validar janela de cancelamento
        Integer cancelWindow = appointment.getService().getProvider().getCancelWindowMinutes();
        if (cancelWindow != null) {
            OffsetDateTime minDateTime = appointment.getStartAt().minusMinutes(cancelWindow);
            if (OffsetDateTime.now().isAfter(minDateTime)) {
                throw new InvalidBusinessRuleException(
                    "Cancelamento deve ser feito com até " + cancelWindow + " minutos de antecedência"
                );
            }
        }
        
        appointment.setStatus(AppointmentStatus.CANCELED);
        appointment.setCancelReason(cancelReason);
        appointment.setCanceledAt(OffsetDateTime.now());
        
        return appointmentRepository.save(appointment);
    }
    
    @Transactional
    public AppointmentEntity complete(UUID id) {
        AppointmentEntity appointment = findById(id);
        
        if (appointment.getStatus() != AppointmentStatus.SCHEDULED) {
            throw new InvalidBusinessRuleException("Apenas agendamentos SCHEDULED podem ser concluídos");
        }
        
        appointment.setStatus(AppointmentStatus.COMPLETED);
        return appointmentRepository.save(appointment);
    }
    
    private void checkConflict(UUID providerId, OffsetDateTime startAt, OffsetDateTime endAt) {
        Specification<AppointmentEntity> spec = AppointmentSpecifications.byProviderId(providerId)
            .and(AppointmentSpecifications.byStatus(AppointmentStatus.SCHEDULED))
            .and(AppointmentSpecifications.overlapsRange(startAt, endAt));
        
        if (appointmentRepository.findOne(spec).isPresent()) {
            throw new AppointmentConflictException("Existe conflito de agendamento nesse horário");
        }
    }
    
    private void checkTimeOff(UUID providerId, OffsetDateTime startAt, OffsetDateTime endAt) {
        boolean hasTimeOff = providerTimeOffRepository.findAll((root, query, cb) -> {
            return cb.and(
                cb.equal(root.get("provider").get("id"), providerId),
                cb.lessThan(root.get("startAt"), endAt),
                cb.greaterThan(root.get("endAt"), startAt)
            );
        }).stream().findAny().isPresent();
        
        if (hasTimeOff) {
            throw new AppointmentConflictException("Provider está em timeOff nesse período");
        }
    }
}
