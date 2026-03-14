package com.pm.inventoryservice.controller;

import com.pm.inventoryservice.service.Purchase;
import lombok.RequiredArgsConstructor;
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
    public String buyProduct(@PathVariable String productId){
        return purchase.purchaseItem(productId);
    }
}
