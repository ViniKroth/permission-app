package com.townsq.vinikroth.permissions.service.dto;

import java.util.Map;

public class GroupPermissions {
    private Map<Integer, RolePermissions> buildingManagerPermissions;
    private Map<Integer, RolePermissions> residentPermissions;

    public GroupPermissions(Map<Integer, RolePermissions> buildingManagerPermissions,
                            Map<Integer, RolePermissions> residentPermissions) {
        this.buildingManagerPermissions = buildingManagerPermissions;
        this.residentPermissions = residentPermissions;
    }

    public Map<Integer, RolePermissions> getBuildingManagerPermissions() {
        return buildingManagerPermissions;
    }

    public void setBuildingManagerPermissions(Map<Integer, RolePermissions> buildingManagerPermissions) {
        this.buildingManagerPermissions = buildingManagerPermissions;
    }

    public Map<Integer, RolePermissions> getResidentPermissions() {
        return residentPermissions;
    }

    public void setResidentPermissions(Map<Integer, RolePermissions> residentPermissions) {
        this.residentPermissions = residentPermissions;
    }
}
