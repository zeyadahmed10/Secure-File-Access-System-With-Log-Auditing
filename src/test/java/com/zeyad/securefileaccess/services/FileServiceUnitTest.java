package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.dao.FileDAO;
import com.zeyad.securefileaccess.dao.UserEntityDAO;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.UserEntity;
import com.zeyad.securefileaccess.exceptions.ResourceNotFoundException;
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
import static org.mockito.Mockito.*;

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
            var userEntity = UserEntity.builder().username("username"+i).id("id"+1).build();
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
    void testFindById_whenTheFileIdExists_shouldReturnFileResponseDto() {
        Integer id = 1;
        doReturn(fileEntityList.get(id)).when(fileDAO).getFileById(any(Integer.class));
        var actualResult = fileService.findById(id);
        assertEquals(fileResponseDTOList.get(id), actualResult);
    }
    @Test
    void testFindById_whenTheFileIdDoesNotExists_shouldThrowResourceNotFoundExceptions() {
        Integer id = 7;
        doReturn(null).when(fileDAO).getFileById(any(Integer.class));
        var exception = assertThrows(ResourceNotFoundException.class, ()->
        {
            fileService.findById(id);
        });
        var expectedMessage = "No file with id: " + id;
        assertEquals(expectedMessage, exception.getMessage());
    }
    @Test
    void testGrantAccess_whenTheFileIdDoesNotExists_shouldThrowResourceNotFoundExceptions() {
        Integer id = 7;
        doReturn(null).when(fileDAO).getFileById(any(Integer.class));
        var exception = assertThrows(ResourceNotFoundException.class, ()->
        {
            fileService.grantAccess(id, mock(GrantAccessRequestDTO.class));
        });
        var expectedMessage = "No file with id: " + id;
        assertEquals(expectedMessage, exception.getMessage());
    }
    @Test
    void testGrantAccess_whenTheFileIdExistsAndAllUsernames_shouldExecuteFileDAOAddFileOneTimeOnly() {
        Integer id = 1;
        doReturn(fileEntityList.get(id)).when(fileDAO).getFileById(id);
        doReturn(userEntityList.get(id)).when(userEntityDAO).getUserByUsername(anyString());
        GrantAccessRequestDTO grantAccessRequestDTO = new GrantAccessRequestDTO();
        grantAccessRequestDTO.setUsers(new HashSet<>(Arrays.asList("username1", "username2")));
        fileService.grantAccess(1, grantAccessRequestDTO);

        // Verifying interactions
        verify(fileDAO, times(1)).getFileById(1);
        verify(userEntityDAO, times(2)).getUserByUsername(anyString()); // Assuming two usernames in the list
        verify(fileDAO, times(1)).addFile(fileEntityList.get(id));
    }
    @Test
    void testGrantAccess_whenTheFileIdExistsAndUserNotFound_shouldThrowResourceNotFoundException() {
        Integer id = 1;
        doReturn(fileEntityList.get(id)).when(fileDAO).getFileById(id);
        doReturn(null).when(userEntityDAO).getUserByUsername(anyString());
        GrantAccessRequestDTO grantAccessRequestDTO = new GrantAccessRequestDTO();
        grantAccessRequestDTO.setUsers(new HashSet<>(Arrays.asList("username1")));

        // Verifying interactions
        var exception = assertThrows(ResourceNotFoundException.class, ()->{
           fileService.grantAccess(id, grantAccessRequestDTO);
        });
        verify(fileDAO, times(1)).getFileById(1);
        var expectedMessage = "User with username: "+"username1"+" does not exist";
        assertEquals(expectedMessage, exception.getMessage());
    }
    @Test
    void testDeleteFile_ifFileNotExisted_shouldThrowResourceNotFoundException() {
        doReturn(0L).when(fileDAO).deleteFileEntity(1);
        var exception = assertThrows(ResourceNotFoundException.class, ()->{fileService.deleteFile(1);});
        var expectedMessage = "No file with id: " + 1;
        assertEquals(expectedMessage, exception.getMessage());
    }
    @Test
    void testUpdateFile_whenNewFileRequestDto_shouldReturnFileResponseWithNewData() {
        var fileRequestDto = new FileRequestDTO("newName", "newContent");
        FileEntity fileEntity = FileEntity.builder()
                .content("newContent").name("newName").createdBy(mock(UserEntity.class)).build();
        doReturn(fileEntity).when(fileDAO).updateFileEntity(any(Integer.class),anyString(), anyString(), anyString());
        var actual = fileService.updateFile(1, fileRequestDto);
        assertEquals(fileRequestDto.getName(), actual.getName());
        assertEquals(fileRequestDto.getContent(), actual.getContent());
    }

    @Test
    void testAddFile_whenNewFileRequestDtoPassed_shouldReturnNewFileResponseDtoWithTheGivenData() {
        String userId = "123";
        Jwt token = mock(Jwt.class);
        when(token.getClaimAsString("sub")).thenReturn(userId);
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        var fileRequestDto = new FileRequestDTO("new file name", "content");
        var user = userEntityList.get(1);
        var file = FileEntity.builder().name("new file name").content("content").createdBy(user).build();
        file.getAuthorizedUsers().add(user);
        doReturn(user).when(userEntityDAO).getUserById(anyString());
        doReturn(file).when(fileDAO).addFile(any(FileEntity.class));
        var actual = fileService.addFile(fileRequestDto);
        System.out.println(actual);
        assertEquals("new file name", actual.getName());
        assertEquals("content", actual.getContent());
        assertEquals(user.getUsername(), actual.getCreatedBy());
        assertEquals(user.getUsername(), (String)actual.getAuthorizedUsers().toArray()[0]);

    }
}