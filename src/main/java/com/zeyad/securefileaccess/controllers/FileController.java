package com.zeyad.securefileaccess.controllers;

import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import com.zeyad.securefileaccess.services.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/v1/files")
public class FileController {
    @Autowired
    private FileService fileService;

    @GetMapping
    public FileResponseDTO getFiles(@RequestParam(name = "name", defaultValue = "") String name,
                                    @RequestParam(name = "page", defaultValue = "0") Integer page,
                                    @RequestParam(name = "size", defaultValue = "10") Integer size){
        return fileService.findAll();
    }
    @GetMapping("/{id}")
    public FileResponseDTO getFileById(@PathVariable Integer id){
        return fileService.findAll();
    }
    @PostMapping
    public FileResponseDTO addFile(@RequestBody FileRequestDTO fileRequestDTO){
        return null;
    }
    @PostMapping("/{id}/grant-access")
    public ResponseEntity<String> grantAccess(@PathVariable Integer id, @RequestBody GrantAccessRequestDTO grantAccessRequestDTO){
        return ResponseEntity.ok("Access granted successfully");
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFile(@PathVariable Integer id){
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateFile(@PathVariable Integer id, @RequestBody FileRequestDTO fileRequestDTO){
        return ResponseEntity.noContent().build();
    }

}
