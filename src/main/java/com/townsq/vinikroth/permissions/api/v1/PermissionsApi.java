package com.townsq.vinikroth.permissions.api.v1;


import com.townsq.vinikroth.permissions.service.PermissionsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/permissions/v1")
public class PermissionsApi {

    private static final Logger logger = LoggerFactory.getLogger(PermissionsApi.class);
    private PermissionsService permissionsService;

    public PermissionsApi(PermissionsService permissionsService) {
        this.permissionsService = permissionsService;
    }

    @GetMapping("/{email}")
    public ResponseEntity<String> getUserPermissions(@PathVariable("email") String email) {
        try {
            logger.info("Getting permissions for user with email {}", email);
            return ResponseEntity.ok(permissionsService.findUserPermissionsByEmail(email));
        } catch (NoSuchElementException e) {
            logger.warn("User with email: {} not found in the database", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        } catch (IllegalArgumentException e) {
            logger.warn("User email cannot be null");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User email cannot be null");
        }
    }
}
