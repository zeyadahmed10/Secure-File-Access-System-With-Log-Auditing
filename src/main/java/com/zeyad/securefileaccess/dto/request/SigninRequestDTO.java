package com.zeyad.securefileaccess.dto.request;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SigninRequestDTO {

    private String username;
    private String password;
}
