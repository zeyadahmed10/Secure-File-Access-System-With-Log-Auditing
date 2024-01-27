package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.dao.FileDAO;
import com.zeyad.securefileaccess.dao.UserEntityDAO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.UserEntity;
import org.apache.catalina.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class FileServiceUnitTest {

    @Mock
    private FileDAO fileDAO;

    @Mock
    private UserEntityDAO userEntityDAO;

    @InjectMocks
    private FileService fileService;
    List<FileEntity> fileEntityList = new ArrayList<FileEntity>();
    List<FileResponseDTO> fileResponseDTOList = new ArrayList<FileResponseDTO>();
    List<String> authUsersList = new ArrayList<>();
    List<UserEntity> userEntityList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        for(int i = 0 ; i<4; i++){
            var userEntity = UserEntity.builder().username("user"+i).id("id"+1).build();
            userEntityList.add(userEntity);
            authUsersList.add(userEntity.getUsername());
        }
        for(int i = 0; i< 5; i++){
            var file = new FileEntity(i, "name"+i, "content"+i, Timestamp.from(Instant.now())
            ,"checksum"+i,userEntityList.get(i%2), new HashSet<>(userEntityList.subList(0,3)));
            fileEntityList.add(file);
            fileResponseDTOList.add(new FileResponseDTO(file.getId(), file.getName(), file.getContent(), file.getLastUpdated()
            ,file.getChecksum(), file.getCreatedBy().getUsername(), new HashSet<String>(authUsersList.subList(0,3))));
        }

    }

    @Test
    void findAllForUser_ValidParameters_ReturnFileResponseDTOList() {
        // Arrange
        String name = "";
        int page = 0;
        int size = 10;
        String userId = "123";
        Jwt token = mock(Jwt.class);
        when(token.getClaimAsString("sub")).thenReturn(userId);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        when(fileDAO.getAllFilesForUser(userId, name, page, size)).thenReturn(fileEntityList);
        // Act
        List<FileResponseDTO> actualResult = fileService.findAllForUser(name, page, size);

        // Assert
        assertEquals(fileResponseDTOList, actualResult);
    }

    @Test
    void findAll_whenFileEntitiesReturnedSuccessfully_shouldReturnFileResponseEntityWithSameReturnedFiles() {

        String name = "";
        int page = 0;
        int size = 10;

        when(fileDAO.getAllFiles(name, page, size)).thenReturn(fileEntityList);
        // Act
        List<FileResponseDTO> actualResult = fileService.findAll(name, page, size);

        // Assert
        assertEquals(fileResponseDTOList, actualResult);
    }

    @Test
    void findById() {
    }

    @Test
    void grantAccess() {
    }

    @Test
    void deleteFile() {
    }

    @Test
    void updateFile() {
    }

    @Test
    void addFile() {
    }
}