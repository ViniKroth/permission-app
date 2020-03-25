package com.townsq.vinikroth.permissions.entity;

import com.townsq.vinikroth.permissions.type.Functionality;
import com.townsq.vinikroth.permissions.type.Permission;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.TreeMap;

@Document(collection = "user_permissions")
public class User {
    @Id
    private String email;
    private Map<Integer, Map<Functionality, Permission>> residencePermissions;

    public User() {
        residencePermissions = new TreeMap<>();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Map<Integer, Map<Functionality, Permission>> getResidencePermissions() {
        return residencePermissions;
    }

    public void setResidencePermissions(Map<Integer, Map<Functionality, Permission>> residencePermissions) {
        this.residencePermissions = residencePermissions;
    }

    //todo botar na classe de serviço
    public void updatePermissionIfHigher(Integer residenceId, Functionality functionality, Permission permission) {
        Map<Functionality, Permission> permissions = residencePermissions.get(residenceId);
        if (null == permissions) {
            permissions = new TreeMap<>();
            permissions.put(functionality, permission);
            residencePermissions.put(residenceId, permissions);
        } else {
            Permission currentPermission = permissions.get(functionality);
            if (null == currentPermission || permission.getValue() > currentPermission.getValue())
                permissions.put(functionality, permission);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", residencePermissions=" + residencePermissions +
                '}';
    }
}

