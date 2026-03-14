package com.pm.orderservice.service;

import com.pm.orderservice.model.Order;
import com.pm.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderRepository orderRepository;

    @KafkaListener(topics = "sale-orders", groupId = "order-group")
    public void consume(String message){

        System.out.println("Received Order: " + message);

        String[] res = message.split(":");

        UUID orderId = UUID.fromString(res[0]);
        String productId = res[1];

        Order order = new Order();
        order.setId(orderId);
        order.setProductId(productId);
        order.setStatus("Success");

        orderRepository.save(order);

        System.out.println("Order with Id: " + orderId + "has been saved to the database!");
    }
}
