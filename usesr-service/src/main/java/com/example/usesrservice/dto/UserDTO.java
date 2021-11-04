package com.example.usesrservice.dto;

import com.example.usesrservice.vo.ResponseOrder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class UserDTO {
// 중간단계의 클래스로 이동할때 사용하는

    private String email;
    private String name;
    private String pwd;
    private String userId;
    private Date createAt;

    private String encryptedPwd;

    //주문
    private List<ResponseOrder> orders;


}
