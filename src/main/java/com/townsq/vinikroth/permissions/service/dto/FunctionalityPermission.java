package com.townsq.vinikroth.permissions.service.dto;

import com.townsq.vinikroth.permissions.type.Functionality;
import com.townsq.vinikroth.permissions.type.Permission;

public class FunctionalityPermission {

    private Functionality functionality;
    private Permission permission;

    public FunctionalityPermission(Functionality functionality, Permission permission) {
        this.functionality = functionality;
        this.permission = permission;
    }

    public Functionality getFunctionality() {
        return functionality;
    }

    public void setFunctionality(Functionality functionality) {
        this.functionality = functionality;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }


}

