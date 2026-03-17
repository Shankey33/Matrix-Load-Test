package com.pm.orderservice.service;

import com.pm.common.dto.OrderEventDTO;
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
    public void consume(OrderEventDTO orderEventDTO){

        System.out.println("Received Order: " + orderEventDTO.getOrderId());
        UUID orderId = UUID.fromString(orderEventDTO.getOrderId());

        Order order = new Order();
        order.setId(orderId);
        order.setProductId(orderEventDTO.getProductId());
        order.setStatus("Success");

        orderRepository.save(order);

        System.out.println("Order with Id: " + orderId + "has been successfully fulfilled!");
    }
}
