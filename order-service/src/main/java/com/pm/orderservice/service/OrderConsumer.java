package com.pm.orderservice.service;

import com.pm.common.dto.OrderEventDTO;
import com.pm.orderservice.model.Order;
import com.pm.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderConsumer {
    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;


    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            autoCreateTopics = "true",
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(topics = "sale-orders", groupId = "order-group")
    public void consume(OrderEventDTO orderEventDTO){

        log.info("Received order: " + orderEventDTO.getOrderId());

        //Duplicate order
        if (orderRepository.existsByOrderId(orderEventDTO.getOrderId())) {
            log.warn("Order {} already processed. Skipping.", orderEventDTO.getOrderId());
            return;
        }

        Order order = new Order();
        order.setOrderId(orderEventDTO.getOrderId());
        order.setProductId(orderEventDTO.getProductId());
        order.setStatus("Success");

        orderRepository.save(order);

        log.info("Order with Id: " + orderEventDTO.getOrderId() + "has been successfully fulfilled!");
    }

    @DltHandler
    public void handleDlt(OrderEventDTO orderEventDTO){
        log.error("Order processing failed for oder: " + orderEventDTO.getOrderId() + " .Reverting back stock!");
        kafkaTemplate.send("stock-revert", orderEventDTO.getProductId());
    }
}
