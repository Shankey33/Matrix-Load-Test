package com.pm.inventoryservice.controller;

import com.pm.inventoryservice.service.Purchase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class StockController {

    private final Purchase purchase;

    @PostMapping("/buy/{productId}")
    public ResponseEntity<String> buyProduct(@PathVariable String productId){
        if(purchase.purchaseItem(productId)){
            return ResponseEntity.ok("Order Accepted");
        } else {
            return ResponseEntity.status(409).body("Sold Out");
        }
    }
}
