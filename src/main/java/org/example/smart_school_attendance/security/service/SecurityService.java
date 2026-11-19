package org.example.smart_school_attendance.security.service;

import org.example.smart_school_attendance.entity.user.Role;
import org.example.smart_school_attendance.security.SecurityUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class SecurityService {

    public SecurityUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("No authenticated user found in SecurityContext");
        }

        return (SecurityUser) authentication.getPrincipal();
    }

    public Long getCurrentUserId() {
        return getCurrentUser().getId();
    }

    public Role getCurrentUserRole() {
        return getCurrentUser().getRole();
    }

    public boolean isSystemAdmin() {
        return getCurrentUserRole() == Role.SYSTEM_ADMIN;
    }

    public Long getCurrentUserSchoolId() {
        return getCurrentUser().getSchoolId();
    }

    public boolean hasAccessToSchoolUpdate(Long targetSchoolId) {
        if (targetSchoolId == null) {
            return false;
        }

        if (isSystemAdmin()) {
            return true;
        }
        Long currentUserSchoolId = getCurrentUserSchoolId();
        return currentUserSchoolId != null && currentUserSchoolId.equals(targetSchoolId) && getCurrentUserRole() == Role.SCHOOL_ADMIN;
    }

    public void validateSchoolAccess(Long targetSchoolId) {
        if (!hasAccessToSchoolUpdate(targetSchoolId)) {
            throw new AccessDeniedException("Access denied: You do not have permission to access school with ID: " + targetSchoolId);
        }
    }
}
