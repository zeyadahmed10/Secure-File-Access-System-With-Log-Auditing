package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.annotation.AuditLogs;
import com.zeyad.securefileaccess.annotation.CheckFileAuthority;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/v1/files")
@Tag(name = "File Controller", description = "APIs for files management and granting access to files for users")
public class FileController {
    @Autowired
    private FileService fileService;
    @Operation(summary = "", description = "")
    @ApiResponse()
    @AuditLogs
    @GetMapping
    public List<FileResponseDTO> getFiles(@Parameter() @RequestParam(name = "name", defaultValue = "") String name,
                                          @Parameter() @RequestParam(name = "page", defaultValue = "0") Integer page,
                                          @Parameter() @RequestParam(name = "size", defaultValue = "10") Integer size){
        return fileService.findAllForUser(name, page, size);
    }
    @AuditLogs
    @CheckFileAuthority
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
    public FileResponseDTO updateFile(@PathVariable Integer id, @RequestBody FileRequestDTO fileRequestDTO){
        return fileService.updateFile(id, fileRequestDTO);
    }
}
