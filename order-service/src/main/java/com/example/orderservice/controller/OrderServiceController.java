package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDTO;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.messagequeue.OrderProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order-service")
public class OrderServiceController {

    OrderService orderService;
    Environment env;
    KafkaProducer kafkaProducer;

    OrderProducer orderProducer;


    @Autowired
    public OrderServiceController(Environment env,
                                  OrderService orderService,
                                  KafkaProducer kafkaProducer,
                                  OrderProducer orderProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
        this.orderProducer = orderProducer;
    }
    @GetMapping("/health_check")
    public String status(){
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }
    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails){

        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDTO orderDTO = mapper.map(orderDetails, OrderDTO.class);    // RequestOrder??? ?????? OrderDTO??? ??????
        orderDTO.setUserId(userId); //PathVariable??? ?????? ????????? userId??? orderDTO??? ??????

        /* jpa ?????? */
        OrderDTO createOrder = orderService.createOrder(orderDTO);       // ????????? orderDTO??? ?????? ???????????? ?????? ??????1
//        ResponseOrder responseOrder = mapper.map(createOrder, ResponseOrder.class); //????????????????????? ?????? ??? ??????????????? ?????? ???????????? ????????? ?????? responseOrder??? ??????

        /* kafka ?????? */
//        orderDTO.setOrderId(UUID.randomUUID().toString());
//        orderDTO.setTotalPrice(orderDetails.getQty()*orderDetails.getUnitPrice());

        /* send this order to the kafka */
//        kafkaProducer.send("example-catalog-topic", orderDTO);
//        orderProducer.send("orders", orderDTO);
        /* topic??? ???????????? ?????? */

        ResponseOrder responseOrder = mapper.map(orderDTO, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);   //responseOrder??? ????????????(????????? : 201)??? ????????? ????????? ????????? ??? ?????????
   }
    // ???????????? ?????? ?????? ?????? ??????
    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId){
        Iterable<OrderEntity> orderList = orderService.getOrderByUserId(userId);

        List<ResponseOrder> result = new ArrayList<>();
        orderList.forEach(v -> {
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
