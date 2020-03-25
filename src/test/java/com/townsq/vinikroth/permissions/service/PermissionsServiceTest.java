package com.townsq.vinikroth.permissions.service;

import com.townsq.vinikroth.permissions.entity.User;
import com.townsq.vinikroth.permissions.entity.UserCacheDocument;
import com.townsq.vinikroth.permissions.exception.IllegalFileElementException;
import com.townsq.vinikroth.permissions.exception.IllegalTextFormatException;
import com.townsq.vinikroth.permissions.repository.PermissionsCacheRepository;
import com.townsq.vinikroth.permissions.repository.PermissionsRepository;
import com.townsq.vinikroth.permissions.type.Functionality;
import com.townsq.vinikroth.permissions.type.Permission;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Permissions;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PermissionsServiceTest {

    @Mock
    private PermissionsRepository permissionsRepository;

    @Mock
    private PermissionsCacheRepository permissionsCacheRepository;

    @InjectMocks
    private PermissionsService permissionsService;

    @Test
    public void shouldBuildApplicationDataSuccessfully() {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("testfileSuccessful.txt");
        Stream<String> content = new BufferedReader(new InputStreamReader(inputStream)).lines();
        List<User> users = EnhancedRandomBuilder.aNewEnhancedRandom().objects(User.class, 1).collect(Collectors.toList());
        doNothing().when(permissionsRepository).deleteAll();
        doNothing().when(permissionsCacheRepository).deleteAll();
        when(permissionsRepository.saveAll(anyCollection())).thenReturn(users);
        List<User> returnedUsers = permissionsService.buildApplicationData(content);
        Assert.assertEquals(users.get(0), returnedUsers.get(0));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionDueToNullContent() {
        permissionsService.buildApplicationData(null);
    }

    @Test(expected = IllegalFileElementException.class)
    public void shouldThrowIllegalFileElementExceptionDueToInvalidLineType(){
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("testfileFailDueToInvalidLineType.txt");
        Stream<String> content = new BufferedReader(new InputStreamReader(inputStream)).lines();
        permissionsService.buildApplicationData(content);
    }

    @Test(expected = IllegalFileElementException.class)
    public void shouldThrowIllegalFileElementExceptionDueToInvalidUserRole() {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("testfileFailDueToInvalidUserRole.txt");
        Stream<String> content = new BufferedReader(new InputStreamReader(inputStream)).lines();
        permissionsService.buildApplicationData(content);
    }

    @Test(expected = IllegalTextFormatException.class)
    public void shouldThrowIllegalTextExceptionDueToManyLineElements() {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("testfileShouldFailDueToManyElementsInLine.txt");
        Stream<String> content = new BufferedReader(new InputStreamReader(inputStream)).lines();
        permissionsService.buildApplicationData(content);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionDueToInvalidEnumProvided() {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("testfileFailDueToInvalidEnum.txt");
        Stream<String> content = new BufferedReader(new InputStreamReader(inputStream)).lines();
        permissionsService.buildApplicationData(content);
    }

    @Test(expected = IllegalFileElementException.class)
    public void shouldThrowIllegalFileElementExceptionDueToIllegalNumericIdProvided() throws IOException {
        InputStream inputStream = this.getClass().getClassLoader()
                .getResourceAsStream("testfileFailDueToIllegalNumericId.txt");
        Stream<String> content = new BufferedReader(new InputStreamReader(inputStream)).lines();
        permissionsService.buildApplicationData(content);
    }

    @Test
    public void shouldFindPermissionsByEmailSuccessfully(){
        String email = "email.com";
        String expected = "\n1;[(Entregas,Leitura)]";

        when(permissionsCacheRepository.findById(email)).thenReturn(Optional.empty());
        when(permissionsCacheRepository.save(any())).thenReturn(new UserCacheDocument(email, "text"));
        when(permissionsRepository.findById(email)).thenReturn(Optional.of(buildUser()));
        String actual = permissionsService.findUserPermissionsByEmail(email);
        Assert.assertEquals(expected, actual);
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowNoSuchElementException(){
        String email = "emailinvalido.com";

        when(permissionsCacheRepository.findById(email)).thenReturn(Optional.empty());
        when(permissionsRepository.findById(email)).thenReturn(Optional.empty());
        String actual = permissionsService.findUserPermissionsByEmail(email);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIllegalArgumentExceptionDueToNullEmail(){
        permissionsService.findUserPermissionsByEmail(null);
    }


    private static User buildUser(){
        User user = new User();
        Map<Integer, Map<Functionality, Permission>> permissions = new TreeMap<>();
        Map<Functionality, Permission> functionalityPermissionMap = new TreeMap<>();
        functionalityPermissionMap.put(Functionality.Entregas, Permission.Leitura);
        permissions.put(1, functionalityPermissionMap);
        user.setResidencePermissions(permissions);
        return user;
    }

}
