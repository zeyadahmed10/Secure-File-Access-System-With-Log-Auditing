package com.zeyad.securefileaccess.mapper;

import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FileResponseDtoMapperUnitTest {
    @Mock
    private FileEntity fileEntityMock = mock(FileEntity.class);

    @Mock
    private UserEntity userEntityMock = mock(UserEntity.class);


    @Test
    public void testMapSingleFileEntity_whenFileEntity_ShouldReturnFileResponseDto() {
        when(fileEntityMock.getId()).thenReturn(1);
        when(fileEntityMock.getName()).thenReturn("testFile");
        when(fileEntityMock.getContent()).thenReturn("testContent");
        when(fileEntityMock.getChecksum()).thenReturn("testChecksum");
        when(fileEntityMock.getLastUpdated()).thenReturn(Timestamp.from(Instant.now()));
        when(fileEntityMock.getCreatedBy()).thenReturn(userEntityMock);
        when(userEntityMock.getUsername()).thenReturn("testUser");

        FileResponseDTO resultDTO = FileResponseDtoMapper.map(fileEntityMock);

        assertEquals(1, resultDTO.getId());
        assertEquals("testFile", resultDTO.getName());
        assertEquals("testContent", resultDTO.getContent());
        assertEquals("testChecksum", resultDTO.getChecksum());
        assertEquals("testUser", resultDTO.getCreatedBy());
    }

    @Test
    public void testMapListFileEntities_whenListOfFileEntity_ShouldReturnListOfFileResponseDto() {
        when(fileEntityMock.getId()).thenReturn(1);
        when(fileEntityMock.getName()).thenReturn("testFile");
        when(fileEntityMock.getContent()).thenReturn("testContent");
        when(fileEntityMock.getChecksum()).thenReturn("testChecksum");
        when(fileEntityMock.getLastUpdated()).thenReturn(java.sql.Timestamp.from(Instant.now()));
        when(fileEntityMock.getCreatedBy()).thenReturn(userEntityMock);
        when(userEntityMock.getUsername()).thenReturn("testUser");

        List<FileEntity> fileEntities = Collections.singletonList(fileEntityMock);

        List<FileResponseDTO> resultDTOs = FileResponseDtoMapper.map(fileEntities);

        assertEquals(1, resultDTOs.size());
        FileResponseDTO resultDTO = resultDTOs.get(0);
        assertEquals(1, resultDTO.getId());
        assertEquals("testFile", resultDTO.getName());
        assertEquals("testContent", resultDTO.getContent());
        assertEquals("testChecksum", resultDTO.getChecksum());
        assertEquals("testUser", resultDTO.getCreatedBy());
    }
}