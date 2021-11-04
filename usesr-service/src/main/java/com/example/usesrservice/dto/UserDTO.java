package com.example.usesrservice.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserDTO {
// 중간단계의 클래스로 이동할때 사용하는

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createAt;

    private String encryptedPwd;


}
