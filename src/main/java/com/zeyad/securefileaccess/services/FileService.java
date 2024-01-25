package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.dao.FileDAO;
import com.zeyad.securefileaccess.dao.UserEntityDAO;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.entity.FileEntity;
import com.zeyad.securefileaccess.entity.UserEntity;
import com.zeyad.securefileaccess.exceptions.ResourceNotFoundException;
import com.zeyad.securefileaccess.mapper.FileResponseDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileService {
    @Autowired
    private FileDAO fileDAO;
    @Autowired
    private UserEntityDAO userEntityDAO;
    public List<FileResponseDTO> findAllForUser(){
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt token = (Jwt) principal;
        String userId = token.getClaimAsString("sub");
        return FileResponseDtoMapper.map(fileDAO.getAllFilesForUser(userId));
    }
    public List<FileResponseDTO> findAll(){
        return FileResponseDtoMapper.map(fileDAO.getAllFiles());
    }

    public FileResponseDTO findById(Integer id) {
        var file = fileDAO.getFileById(id);
        if(file ==null)
            throw new ResourceNotFoundException("No file with id: "+id);
        return FileResponseDtoMapper.map(file);
    }

    public void grantAccess(Integer id, GrantAccessRequestDTO grantAccessRequestDTO) {
        var file = fileDAO.getFileById(id);
        if(file ==null)
            throw new ResourceNotFoundException("No file with id: "+id);
        List<UserEntity> users = new ArrayList<UserEntity>();
        for(var name: grantAccessRequestDTO.getUsers()){
            UserEntity user = userEntityDAO.getUserByUsername(name);
            if(user !=null)
                throw new ResourceNotFoundException("User with username: "+name);
            users.add(user);
        }
        file.getAuthorizedUsers().addAll(users);
    }

    public void deleteFile(Integer id) {
        var affectedRows = fileDAO.deleteFileEntity(id);
        if(affectedRows==0)
            throw new ResourceNotFoundException("No file with id: "+id);
    }

    public FileResponseDTO updateFile(Integer id, FileRequestDTO fileRequestDTO) {
        return FileResponseDtoMapper.map(fileDAO.updateFileEntity(id, fileRequestDTO.getName(), fileRequestDTO.getContent()
        ,calculateSHA256Checksum(fileRequestDTO.getContent())));
    }


    public FileResponseDTO addFile(FileRequestDTO fileRequestDTO) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt token = (Jwt) principal;
        String userId = token.getClaimAsString("sub");
        FileEntity fileEntity = FileEntity.builder()
                .name(fileRequestDTO.getName()).content(fileRequestDTO.getContent())
                .checksum(calculateSHA256Checksum(fileRequestDTO.getContent()))
                .lastUpdated(Timestamp.from(Instant.now()))
                .build();
        var user = userEntityDAO.getUserById(userId);
        fileEntity.getAuthorizedUsers().add(user);
        fileEntity.setCreatedBy(user);
        return FileResponseDtoMapper.map(fileDAO.addFile(fileEntity));
    }

    public String calculateSHA256Checksum(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Convert the byte array to a hexadecimal string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            // Handle exception (e.g., log or throw a runtime exception)
            e.printStackTrace();
            return null;
        }
    }
}
