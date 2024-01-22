package com.zeyad.securefileaccess.services;

import com.zeyad.securefileaccess.dto.response.FileResponseDTO;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    public FileResponseDTO findAll(){
        return new FileResponseDTO();
    }
}
