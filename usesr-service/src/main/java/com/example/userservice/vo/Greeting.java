package com.example.userservice.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component  // service, controller 등의 용도가 불분명한 상태에서 bean으로 등록하고자 할 때 사용
@Data       // Lombok에 내장된 기능으로, getter-setter를 구현하지 않아도 됨.
public class Greeting {

    @Value("${greeting.message}")
    private String message;


}
