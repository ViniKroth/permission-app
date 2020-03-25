package com.townsq.vinikroth.permissions.service.dto;

import com.townsq.vinikroth.permissions.type.Functionality;
import com.townsq.vinikroth.permissions.type.Permission;

import java.util.ArrayList;
import java.util.List;

public class RolePermissions {
    private List<FunctionalityPermission> permissions;

    public RolePermissions() {
        permissions = new ArrayList<>();
    }

    public void addPermission(Functionality functionality, Permission permission) {
        permissions.add(new FunctionalityPermission(functionality, permission));
    }

    public List<FunctionalityPermission> getPermissions() {
        return permissions;
    }
}
