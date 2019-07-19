package com.lgmn.iotserver.dto;

import lombok.Data;

@Data
public class LoginDto {
    private String phone;
    private String password;
}