package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.jpa.OrderEntity;

public interface OrderService {
    // 단일값을 저장하기 위한= 주문
    OrderDTO createOrder(OrderDTO orderDetails);

    // 주문아이디를 가지고 검색
    OrderDTO getOrderByOrderId(String orderId);

    // 전체 주문 목록 가져오기
    Iterable<OrderEntity> getOrderByUserId(String userId);


}
