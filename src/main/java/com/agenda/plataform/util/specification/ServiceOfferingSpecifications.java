package com.agenda.plataform.util.specification;

import org.springframework.data.jpa.domain.Specification;

import com.agenda.plataform.entity.ServiceOfferingEntity;
import java.util.UUID;

public class ServiceOfferingSpecifications {
    
    public static Specification<ServiceOfferingEntity> byProviderId(UUID providerId) {
        return (root, query, cb) -> cb.equal(root.get("provider").get("id"), providerId);
    }
    
    public static Specification<ServiceOfferingEntity> isActive(Boolean active) {
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
    
    public static Specification<ServiceOfferingEntity> byName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
    
    public static Specification<ServiceOfferingEntity> byMinPrice(Integer minPriceCents) {
        return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("priceCents"), minPriceCents);
    }
    
    public static Specification<ServiceOfferingEntity> byMaxPrice(Integer maxPriceCents) {
        return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("priceCents"), maxPriceCents);
    }
}
