package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.annotation.AuditLogs;
import com.zeyad.securefileaccess.annotation.CheckFileAuthority;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    @Autowired
    private FileService fileService;
    @AuditLogs
    @GetMapping
    public List<FileResponseDTO> getFiles(@RequestParam(name = "name", defaultValue = "") String name,
                                          @RequestParam(name = "page", defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size){
        return fileService.findAll();
    }
    @AuditLogs
    @CheckFileAuthority
    @GetMapping("/{id}")
    public FileResponseDTO getFileById(@PathVariable Integer id){
        return new FileResponseDTO();
    }
    @AuditLogs
    @PostMapping
    public FileResponseDTO addFile(@RequestBody FileRequestDTO fileRequestDTO){
        fileService.addFile(fileRequestDTO);
        return new FileResponseDTO();
    }
    @AuditLogs
    @CheckFileAuthority
    @PostMapping("/{id}/grant-access")
    public ResponseEntity<String> grantAccess(@PathVariable Integer id, @RequestBody GrantAccessRequestDTO grantAccessRequestDTO){
        fileService.grantAccess(id, grantAccessRequestDTO);
        return ResponseEntity.ok("Access granted successfully");
    }
    @AuditLogs
    @CheckFileAuthority
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id){
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
    @AuditLogs
    @CheckFileAuthority
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFile(@PathVariable Integer id, @RequestBody FileRequestDTO fileRequestDTO){
        fileService.updateFile(id, fileRequestDTO);
        return ResponseEntity.noContent().build();
    }
}
