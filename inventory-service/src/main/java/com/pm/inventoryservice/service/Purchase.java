package com.pm.inventoryservice.service;

import com.pm.common.dto.OrderEventDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class Purchase {
    private final StringRedisTemplate redisTemplate;
    private final KafkaTemplate<String, OrderEventDTO> kafkaTemplate;

    public String purchaseItem(String productId){
        Long remainingStock = redisTemplate.opsForValue().decrement("stock:" + productId);

        if(remainingStock != null && remainingStock >= 0){

            String orderId = UUID.randomUUID().toString();
            OrderEventDTO orderEventDTO = new OrderEventDTO(orderId, productId, "PENDING");
            kafkaTemplate.send("sale-orders", orderId, orderEventDTO);

            return "Order Placed! Order Id: " + orderId;
        } else {
            return "Sold Out";
        }
    }
}
