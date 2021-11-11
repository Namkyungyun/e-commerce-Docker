package com.example.orderservice.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder  //생성자, setter 클래스에서 메소드를 이용해서 데이터를 저자하는 것보다 훨신 직관적
public class Payload {
    private String order_id;
    private String user_id;
    private String product_id;
    private int qty;
    private int unit_price;
    private int total_price;
}
