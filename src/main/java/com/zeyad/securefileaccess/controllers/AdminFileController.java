package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.annotation.AuditLogs;
import com.zeyad.securefileaccess.annotation.CheckFileAuthority;
import com.zeyad.securefileaccess.annotation.CustomPreAuthorize;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/admin/files")
@CustomPreAuthorize(role = "admin")
@RestController
public class AdminFileController {

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
    @GetMapping("/{id}")
    public FileResponseDTO getFileById(@PathVariable Integer id){
        return fileService.findById(id);
    }
    @AuditLogs
    @PostMapping
    public FileResponseDTO addFile(@RequestBody FileRequestDTO fileRequestDTO){
        return fileService.addFile(fileRequestDTO);
    }
    @AuditLogs
    @PostMapping("/{id}/grant-access")
    public ResponseEntity<String> grantAccess(@PathVariable Integer id, @RequestBody GrantAccessRequestDTO grantAccessRequestDTO){
        fileService.grantAccess(id, grantAccessRequestDTO);
        return ResponseEntity.ok("Access granted successfully");
    }
    @AuditLogs
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id){
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }
    @AuditLogs
    @PatchMapping("/{id}")
    public FileResponseDTO updateFile(@PathVariable Integer id, @RequestBody FileRequestDTO fileRequestDTO){
        return fileService.updateFile(id, fileRequestDTO);
    }
}
