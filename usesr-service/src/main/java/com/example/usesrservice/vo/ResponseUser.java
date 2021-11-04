package com.example.usesrservice.vo;

import lombok.Data;

@Data
public class ResponseUser {
    //반환은 세가지의 내용을 보여준다.
    private String email;
    private String name;
    private String userId;

}
