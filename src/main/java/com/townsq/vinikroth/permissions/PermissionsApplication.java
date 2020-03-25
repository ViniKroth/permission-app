package com.townsq.vinikroth.permissions;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.townsq.vinikroth.permissions.*")
public class PermissionsApplication {

    public static void main(String[] args) {
        SpringApplication.run(PermissionsApplication.class, args);
    }
}
