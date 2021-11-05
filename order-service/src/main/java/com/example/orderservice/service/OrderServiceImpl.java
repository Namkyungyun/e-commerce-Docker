package com.example.orderservice.service;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.jpa.OrderRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Data
@Slf4j
public class OrderServiceImpl implements OrderService{

    OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    // 주문하기
    @Override
    public OrderDTO createOrder(OrderDTO orderDetails) {
        orderDetails.setOrderId(UUID.randomUUID().toString());
        orderDetails.setTotalPrice(orderDetails.getQty()*orderDetails.getUnitPrice());

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderEntity orderEntity = mapper.map(orderDetails, OrderEntity.class); // db에 저장하기 위해 entity로 변환

        orderRepository.save(orderEntity);

        OrderDTO returnOrder = mapper.map(orderEntity, OrderDTO.class); //최종적으로 반환될 때는 dto로 변환

        return returnOrder;
    }

    @Override
    public OrderDTO getOrderByOrderId(String orderId) {
        OrderEntity orderEntity = orderRepository.findByOrderId(orderId); //sql로 가져오기

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        OrderDTO orderDTO = mapper.map(orderEntity, OrderDTO.class);

        return orderDTO;
    }


    // 전체 주문 내역 조회
    @Override
    public Iterable<OrderEntity> getOrderByUserId(String userId) {
        return orderRepository.findByUserId(userId);
    }
}
