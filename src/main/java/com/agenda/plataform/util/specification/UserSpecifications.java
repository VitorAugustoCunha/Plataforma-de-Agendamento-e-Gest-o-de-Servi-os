package com.agenda.plataform.util.specification;

import org.springframework.data.jpa.domain.Specification;

import com.agenda.plataform.entity.UserEntity;
import com.agenda.plataform.enums.UserRole;

public class UserSpecifications {
    
    public static Specification<UserEntity> byEmail(String email) {
        return (root, query, cb) -> cb.equal(root.get("email"), email);
    }
    
    public static Specification<UserEntity> byRole(UserRole role) {
        return (root, query, cb) -> cb.equal(root.get("role"), role);
    }
    
    public static Specification<UserEntity> isActive(Boolean active) {
        return (root, query, cb) -> cb.equal(root.get("active"), active);
    }
    
    public static Specification<UserEntity> byName(String name) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }
}
