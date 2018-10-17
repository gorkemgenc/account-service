package com.github.service.accountservice.controller;

import com.github.service.accountservice.aspect.RateLimit;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.exceptions.RateLimiterException;
import com.github.service.accountservice.service.contracts.IProductService;
import com.github.service.accountservice.service.contracts.IStoreService;
import com.github.service.accountservice.service.models.ProductDto;
import com.github.service.accountservice.service.models.TransactionDto;
import com.github.service.accountservice.validator.IValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("/store")
public class StoreController {

    Logger logger = LoggerFactory.getLogger(StoreController.class);

    @Autowired
    IProductService productService;
    @Autowired
    IStoreService storeService;
    @Autowired
    IValidator validator;

    @GetMapping("/list")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    public ResponseEntity<?> getAll() throws RateLimiterException, AccountException {

        logger.debug("StoreController getAll method was called");

        List<ProductDto> products = storeService.listAllAvailableProducts();
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("products", products);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }

    @PostMapping("/buy")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    public ResponseEntity<?> buy(@RequestBody Map<String, String> dataMap) throws RateLimiterException, AccountException {

        logger.debug("StoreController buy method was called");

        validator.validate(dataMap, Arrays.asList("accountId", "productId"));
        String accountId = dataMap.get("accountId");
        String productId = dataMap.get("productId");

        TransactionDto transaction = storeService.buyProduct(Integer.parseInt(accountId), Integer.parseInt(productId));
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("transaction", transaction);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }
}
