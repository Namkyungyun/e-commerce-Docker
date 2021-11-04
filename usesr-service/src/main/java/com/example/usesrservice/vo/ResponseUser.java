package com.example.usesrservice.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseUser {
    //반환은 세가지의 내용을 보여준다.
    private String email;
    private String name;
    private String userId;

    private List<ResponseOrder> orders;
}
