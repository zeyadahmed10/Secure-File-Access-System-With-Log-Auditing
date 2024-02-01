package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.annotation.AuditLogs;
import com.zeyad.securefileaccess.annotation.CheckFileAuthority;
import com.zeyad.securefileaccess.annotation.CustomPreAuthorize;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/v1/admin/files")
@RestController
@Tag(name = "Admin File Controller", description = "APIs for files management and granting access to files for admins")
public class AdminFileController {

    @Autowired
    private FileService fileService;

    @Operation(summary = "Get files", description = "Retrieves a list of all files for all users")
    @ApiResponse(responseCode = "200", description = "List of files retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @ApiResponse(responseCode = "404", description = "No files found with the specified parameters")
    @AuditLogs
    @GetMapping
    @CustomPreAuthorize(role = "admin")
    public List<FileResponseDTO> getFiles(@RequestParam(name = "name", defaultValue = "") String name,
                                          @RequestParam(name = "page", defaultValue = "0") Integer page,
                                          @RequestParam(name = "size", defaultValue = "10") Integer size){
        return fileService.findAll(name, page, size);
    }

    @Operation(summary = "Get file by its id", description = "Retrieves a file by its id")
    @ApiResponse(responseCode = "200", description = "File response")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @AuditLogs
    @GetMapping("/{id}")
    @CustomPreAuthorize(role = "admin")
    public FileResponseDTO getFileById(@PathVariable Integer id){
        return fileService.findById(id);
    }

    @Operation(summary = "Add file", description = "Add new file tagged with the logged admin")
    @ApiResponse(responseCode = "200", description = "File created and returns file response")
    @ApiResponse(responseCode = "401", description = "Unauthorized access you need to login to create file")
    @AuditLogs
    @PostMapping
    @CustomPreAuthorize(role = "admin")
    public FileResponseDTO addFile(@RequestBody FileRequestDTO fileRequestDTO){
        return fileService.addFile(fileRequestDTO);
    }
    @Operation(summary = "Grant access for file for specified users", description = "Grant access for file by its id, by list of specified users usernames")
    @ApiResponse(responseCode = "200", description = "Access granted for given users")
    @ApiResponse(responseCode = "404", description = "User not found with given username, or no file with given id")
    @ApiResponse(responseCode = "401", description = "Unauthorized access need to login before granting access")
    @AuditLogs
    @PostMapping("/{id}/grant-access")
    @CustomPreAuthorize(role = "admin")
    public ResponseEntity<String> grantAccess(@PathVariable Integer id, @RequestBody GrantAccessRequestDTO grantAccessRequestDTO){
        fileService.grantAccess(id, grantAccessRequestDTO);
        return ResponseEntity.ok("Access granted successfully");
    }

    @Operation(summary = "Delete file", description = "Delete file with its id")
    @ApiResponse(responseCode = "201", description = "File deleted successfully")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @AuditLogs
    @DeleteMapping("/{id}")
    @CustomPreAuthorize(role = "admin")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id){
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Updated file", description = "Update file with new content and name")
    @ApiResponse(responseCode = "200", description = "File response with new file updated content")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @AuditLogs
    @PatchMapping("/{id}")
    @CustomPreAuthorize(role = "admin")
    public FileResponseDTO updateFile(@PathVariable Integer id, @RequestBody FileRequestDTO fileRequestDTO){
        return fileService.updateFile(id, fileRequestDTO);
    }
}
