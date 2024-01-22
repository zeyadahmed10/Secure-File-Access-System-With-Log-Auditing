package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.dto.request.FileRequestDTO;
import com.zeyad.securefileaccess.dto.request.GrantAccessRequestDTO;
import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FileService {

    public List<FileResponseDTO> findAll(){
        return null;
    }

    public FileResponseDTO findById() {
        return null;
    }

    public void grantAccess(Integer id, GrantAccessRequestDTO grantAccessRequestDTO) {
    }

    public void deleteFile(Integer id) {
    }

    public void updateFile(Integer id, FileRequestDTO fileRequestDTO) {
    }
}
