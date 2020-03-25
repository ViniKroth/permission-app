package com.townsq.vinikroth.permissions.service;

import com.townsq.vinikroth.permissions.entity.User;
import com.townsq.vinikroth.permissions.entity.UserCacheDocument;
import com.townsq.vinikroth.permissions.exception.IllegalFileElementException;
import com.townsq.vinikroth.permissions.exception.IllegalTextFormatException;
import com.townsq.vinikroth.permissions.repository.PermissionsCacheRepository;
import com.townsq.vinikroth.permissions.repository.PermissionsRepository;
import com.townsq.vinikroth.permissions.service.dto.RolePermissions;
import com.townsq.vinikroth.permissions.service.dto.UserRoles;
import com.townsq.vinikroth.permissions.type.Functionality;
import com.townsq.vinikroth.permissions.type.LineType;
import com.townsq.vinikroth.permissions.type.Permission;
import com.townsq.vinikroth.permissions.type.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Stream;

@Service
public class PermissionsService {

    private static final Integer MIN_LINE_ELEMENTS = 3;
    private static final Integer MAX_LINE_ELEMENTS = 4;
    private static final Logger logger = LoggerFactory.getLogger(PermissionsService.class);

    private PermissionsRepository permissionsRepository;
    private PermissionsCacheRepository permissionsCacheRepository;

    public PermissionsService(PermissionsRepository permissionsRepository,
                              PermissionsCacheRepository permissionsCacheRepository) {
        this.permissionsRepository = permissionsRepository;
        this.permissionsCacheRepository = permissionsCacheRepository;
    }

    @EventListener(ApplicationStartedEvent.class)
    private void loadApplicationData() {
        logger.info("Attempting to load data from text file located in project root resources.");
        try {
            InputStream inputStream = this.getClass().getClassLoader()
                    .getResourceAsStream("basefile.txt");
            buildApplicationData(new BufferedReader(new InputStreamReader(inputStream)).lines());
            logger.info("Data correctly loaded from text file.");
        } catch (Exception e) {
            logger.error("An exception occurred while building application data. Application will start," +
                    " but database will not be populated. Error message: {}", e.getMessage());
        }
    }

    public String findUserPermissionsByEmail(String email) {
        if (null == email) {
            logger.warn("Provided email cannot be null.");
            throw new IllegalArgumentException("Provided email cannot be null");
        }

        Optional<UserCacheDocument> userCacheDocument = permissionsCacheRepository.findById(email);
        if (userCacheDocument.isPresent()) {
            logger.info("Using document attached to email:{} from cache memory.", email);
            return userCacheDocument.get().getFormattedText();
        } else {
            User user = permissionsRepository.findById(email).orElseThrow(NoSuchElementException::new);
            Map<Integer, Map<Functionality, Permission>> permissions = user.getResidencePermissions();
            StringBuilder str = new StringBuilder();

            for (Integer residenceId : permissions.keySet()) {
                Map<Functionality, Permission> residencePermission = permissions.get(residenceId);

                str.append("\n");
                str.append(residenceId);
                str.append(";");
                str.append("[");
                residencePermission.keySet().forEach(functionalityPermission -> {
                    str.append("(");
                    str.append(functionalityPermission.name());
                    str.append(",");
                    str.append(residencePermission.get(functionalityPermission).name());
                    str.append("),");
                });
                str.replace(str.length() - 1, str.length(), "]");
            }
            permissionsCacheRepository.save(new UserCacheDocument(email, str.toString()));
            return str.toString();
        }
    }

    public List<User> buildApplicationData(Stream<String> streamedInput) {
        if (null == streamedInput) {
            logger.error("Provided streamed content is null.");
            throw new IllegalArgumentException("Streamed content cannot be null.");
        }

        MultiValueMap<String, UserRoles> users = new LinkedMultiValueMap<>();
        Map<Integer, RolePermissions> buildingManagerPermissions = new HashMap<>();
        Map<Integer, RolePermissions> residentPermissions = new HashMap<>();

        streamedInput.forEach(line -> {
            String[] splicedLine = line.split(";");

            if (splicedLine.length < MIN_LINE_ELEMENTS || splicedLine.length > MAX_LINE_ELEMENTS) {
                throw new IllegalTextFormatException("Line elements size is not supported in the current configuration.");
            }
            if (splicedLine[0].equals(LineType.Grupo.name())) {
                handleGroupDataStructuring(splicedLine, residentPermissions, buildingManagerPermissions);

            } else if (splicedLine[0].equals(LineType.Usuario.name())) {
                handleUserDataStructuring(splicedLine, users);

            } else {
                logger.error("{}, is not a valid line type", splicedLine[0]);
                throw new IllegalFileElementException("Provided element is not a valid line identifier.");
            }
        });

        logger.info("Data read from text file, proceeding to persist in database.");
        return persistDataInDb(users, residentPermissions, buildingManagerPermissions);
    }

    private void handleUserDataStructuring(String[] splicedLine, MultiValueMap<String, UserRoles> users) {
        String formattedLine = splicedLine[2].replaceAll("[(\\]\\[]", "");
        String[] permissions = formattedLine.split("\\),");
        String userEmail = splicedLine[1];

        for (String permission : permissions) {
            String[] group = permission.replace(")", "").split(",");
            try {
                users.add(userEmail, new UserRoles(UserRole.valueOf(group[0]), new Integer(group[1]
                        .replaceAll(" ", ""))));
            } catch (NumberFormatException e) {
                logger.error("Provided id: {}, it's not a valid number.", group[1]);
                throw new IllegalFileElementException("Provided id is not a valid number");
            }
        }
    }

    private void handleGroupDataStructuring(String[] splicedLine,
                                            Map<Integer, RolePermissions> residentPermissions,
                                            Map<Integer, RolePermissions> buildingManagerPermissions) {
        String formattedLine = splicedLine[3].replaceAll("[(\\]\\[]", "");
        String[] permissions = formattedLine.split("\\),");

        RolePermissions rolePermissions = new RolePermissions();
        for (String permission : permissions) {
            String[] functionalityPermission = permission.replace(")", "").split(",");
            if (functionalityPermission.length != 2) {
                logger.error("Invalid functionality-permission data, expected format is one Functionality and one Permission.");
                throw new IllegalTextFormatException("Too much elements is functionality-permission field");
            }
            rolePermissions.addPermission(Functionality.valueOf(functionalityPermission[0]),
                    Permission.valueOf(functionalityPermission[1]));
        }

        if (splicedLine[1].equals(UserRole.Morador.name())) {
            try {
                residentPermissions.put(new Integer(splicedLine[2].replaceAll(" ", "")), rolePermissions);
            } catch (NumberFormatException e) {
                logger.error("Provided id: {}, it's not a valid number", splicedLine[2]);
                throw new IllegalFileElementException("Provided id is not a valid number");
            }
        } else if (splicedLine[1].equals(UserRole.Sindico.name())) {
            try {
                buildingManagerPermissions.put(new Integer(splicedLine[2].replaceAll(" ", "")), rolePermissions);
            } catch (NumberFormatException e) {
                logger.error("Provided id: {}, it's not a valid number", splicedLine[2]);
                throw new IllegalFileElementException("Provided id is not a valid number");
            }
        } else {
            throw new IllegalFileElementException("Provided element is not a valid user role.");
        }
    }

    private List<User> persistDataInDb(MultiValueMap<String, UserRoles> users,
                                       Map<Integer, RolePermissions> residentPermissions,
                                       Map<Integer, RolePermissions> buildingManagerPermissions) {
        List<User> userEntities = new ArrayList<>();

        users.keySet()
                .stream()
                .forEach(user -> {
                    User toBePersistedUser = new User();
                    toBePersistedUser.setEmail(user);

                    users.get(user).forEach(userRole -> {
                        RolePermissions rolePermissions;
                        if (userRole.getRole().equals(UserRole.Morador)) {
                            rolePermissions = residentPermissions.get(userRole.getResidenceId());
                        } else {
                            rolePermissions = buildingManagerPermissions.get(userRole.getResidenceId());
                        }

                        if (null == rolePermissions) {
                            logger.warn("Group id: {}, referenced by user: {} does not have an line entry, this group" +
                                    "reference will be ignored and startup will continue normally.", userRole.getResidenceId(), user);
                            return;
                        }
                        rolePermissions.getPermissions().forEach(permission -> {
                            toBePersistedUser.updatePermissionIfHigher(userRole.getResidenceId(),
                                    permission.getFunctionality(), permission.getPermission());
                        });
                    });
                    userEntities.add(toBePersistedUser);
                });

        permissionsCacheRepository.deleteAll();
        permissionsRepository.deleteAll();
        return permissionsRepository.saveAll(userEntities);
    }
}
