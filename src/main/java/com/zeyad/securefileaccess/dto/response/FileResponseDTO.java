package com.zeyad.securefileaccess.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDTO {

    private Integer id;
    private String name;
    private String content;
    private Timestamp lastUpdated;
    private String checksum;
    private String createdBy;
    private Set<String> authorizedUsers;
}
