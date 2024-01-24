package com.zeyad.securefileaccess.mapper;

import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.UserEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileResponseDtoMapper {
    public static FileResponseDTO map(FileEntity fileEntity){
       return FileResponseDTO.builder()
               .id(fileEntity.getId()).name(fileEntity.getName())
               .content(fileEntity.getContent()).checksum(fileEntity.getChecksum())
               .createdBy(fileEntity.getCreatedBy().getUsername()).lastUpdated(fileEntity.getLastUpdated())
               .authorizedUsers(fileEntity.getAuthorizedUsers().stream().map(UserEntity::getUsername).collect(Collectors.toSet())).build();
    }
    public static List<FileResponseDTO> map(List<FileEntity> fileEntityList){
        List<FileResponseDTO> responseDTOs = new ArrayList<>();
        for(var item: fileEntityList){
            responseDTOs.add(FileResponseDtoMapper.map(item));
        }
        return responseDTOs;
    }
}
