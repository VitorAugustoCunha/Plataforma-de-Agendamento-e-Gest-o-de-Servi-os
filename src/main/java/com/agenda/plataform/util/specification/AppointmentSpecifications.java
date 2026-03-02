package com.agenda.plataform.util.specification;

import org.springframework.data.jpa.domain.Specification;

import com.agenda.plataform.entity.AppointmentEntity;
import com.agenda.plataform.enums.AppointmentStatus;
import java.time.OffsetDateTime;
import java.util.UUID;

public class AppointmentSpecifications {
    
    public static Specification<AppointmentEntity> byClientId(UUID clientId) {
        return (root, query, cb) -> cb.equal(root.get("client").get("id"), clientId);
    }
    
    public static Specification<AppointmentEntity> byProviderId(UUID providerId) {
        return (root, query, cb) -> cb.equal(root.get("provider").get("id"), providerId);
    }
    
    public static Specification<AppointmentEntity> byStatus(AppointmentStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }
    
    public static Specification<AppointmentEntity> byServiceId(UUID serviceId) {
        return (root, query, cb) -> cb.equal(root.get("service").get("id"), serviceId);
    }
    
    public static Specification<AppointmentEntity> startsAfter(OffsetDateTime dateTime) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startAt"), dateTime);
    }
    
    public static Specification<AppointmentEntity> startsBefore(OffsetDateTime dateTime) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("startAt"), dateTime);
    }
    
    public static Specification<AppointmentEntity> overlapsRange(OffsetDateTime start, OffsetDateTime end) {
        return (root, query, cb) -> cb.and(
            cb.lessThan(root.get("startAt"), end),
            cb.greaterThan(root.get("endAt"), start)
        );
    }
}
