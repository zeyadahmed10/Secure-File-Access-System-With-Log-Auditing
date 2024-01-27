package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.annotation.AuditLogs;
import com.zeyad.securefileaccess.annotation.CheckFileAuthority;
import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Operation(summary = "Get files", description = "Retrieves a list of files that the logged user have access for.")
    @ApiResponse(responseCode = "200", description = "List of files retrieved successfully.")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @ApiResponse(responseCode = "404", description = "No files found for the logged user or with the specified parameters")
    @AuditLogs
    @GetMapping
    public List<FileResponseDTO> getFiles(
            @Parameter(description = "Name of the file") @RequestParam(name = "name", defaultValue = "") String name,
            @Parameter(description = "Page number") @RequestParam(name = "page", defaultValue = "0") Integer page,
            @Parameter(description = "Page size") @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return fileService.findAllForUser(name, page, size);
    }

    @Operation(summary = "Get file by its id", description = "Retrieves a file by its id for logged user if it has the required authority")
    @ApiResponse(responseCode = "200", description = "File response")
    @ApiResponse(responseCode = "403", description = "forbidden doesn't have the required authority to access this file")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @AuditLogs
    @CheckFileAuthority
    @GetMapping("/{id}")
    public FileResponseDTO getFileById(@PathVariable Integer id){
        return fileService.findById(id);
    }

    @Operation(summary = "Add file", description = "Add new file tagged with the logged user")
    @ApiResponse(responseCode = "200", description = "File created and returns file response")
    @ApiResponse(responseCode = "401", description = "Unauthorized access you need to login to create file")
    @AuditLogs
    @PostMapping
    public FileResponseDTO addFile(@RequestBody FileRequestDTO fileRequestDTO){
        return fileService.addFile(fileRequestDTO);
    }

    @Operation(summary = "Grant access for file for specified users", description = "Grant access for file by its id if the logged user have the authority, by list of specified users usernames")
    @ApiResponse(responseCode = "200", description = "Access granted for given users")
    @ApiResponse(responseCode = "404", description = "User not found with given username, or no file with given id")
    @ApiResponse(responseCode = "403", description = "Logged user doesn't acquire the authority to give access for other users")
    @ApiResponse(responseCode = "401", description = "Unauthorized access need to login before granting access")
    @AuditLogs
    @CheckFileAuthority
    @PostMapping("/{id}/grant-access")
    public ResponseEntity<String> grantAccess(@PathVariable Integer id, @RequestBody GrantAccessRequestDTO grantAccessRequestDTO){
        fileService.grantAccess(id, grantAccessRequestDTO);
        return ResponseEntity.ok("Access granted successfully");
    }

    @Operation(summary = "Delete file", description = "Delete file with its id for the logged user")
    @ApiResponse(responseCode = "201", description = "File deleted successfully")
    @ApiResponse(responseCode = "403", description = "forbidden doesn't have the required authority to access this file")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @AuditLogs
    @CheckFileAuthority
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id){
        fileService.deleteFile(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Updated file", description = "Update file with new content and name")
    @ApiResponse(responseCode = "200", description = "File response with new file updated content")
    @ApiResponse(responseCode = "403", description = "forbidden doesn't have the required authority to access this file")
    @ApiResponse(responseCode = "404", description = "File not found")
    @ApiResponse(responseCode = "401", description = "Unauthorized access")
    @AuditLogs
    @CheckFileAuthority
    @PatchMapping("/{id}")
    public FileResponseDTO updateFile(@PathVariable Integer id, @RequestBody FileRequestDTO fileRequestDTO){
        return fileService.updateFile(id, fileRequestDTO);
    }
}
