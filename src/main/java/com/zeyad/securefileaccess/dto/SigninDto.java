package com.zeyad.securefileaccess.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SigninDto {

    private String username;
    private String password;
}
