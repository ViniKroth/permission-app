package com.townsq.vinikroth.permissions.service.dto;

import com.townsq.vinikroth.permissions.type.UserRole;

public class UserRoles {

    private UserRole role;
    private Integer residenceId;

    public UserRoles(UserRole role, Integer residenceId) {
        this.role = role;
        this.residenceId = residenceId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public int getResidenceId() {
        return residenceId;
    }

    public void setResidenceId(Integer residenceId) {
        this.residenceId = residenceId;
    }
}
