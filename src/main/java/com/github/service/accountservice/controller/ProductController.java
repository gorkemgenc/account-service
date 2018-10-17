package com.github.service.accountservice.controller;

import com.github.service.accountservice.aspect.RateLimit;
import com.github.service.accountservice.exceptions.AccountException;
import com.github.service.accountservice.exceptions.RateLimiterException;
import com.github.service.accountservice.service.consumes.ProductCreate;
import com.github.service.accountservice.service.consumes.ProductDelete;
import com.github.service.accountservice.service.contracts.IProductService;
import com.github.service.accountservice.service.models.ProductDto;
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
@RequestMapping("/product")
public class ProductController {

    Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    IProductService productService;
    @Autowired
    IValidator validator;

    @PostMapping("/create")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    public ResponseEntity<?> create(@RequestBody HashMap<String,ProductCreate> dataMap) throws RateLimiterException, AccountException {

        logger.info("ProductController create method calls for creating product");

        validator.createProductValidate(dataMap, Arrays.asList("product"));

        ProductCreate product = dataMap.get("product");
        ProductDto createdProduct = productService.createProduct(product);
        logger.info("ProductController create method created product");
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("product", createdProduct);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }

    @GetMapping("/list")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    public ResponseEntity<?> getAll() throws RateLimiterException, AccountException {

        logger.info("ProductController getAll method was called");

        List<ProductDto> products = productService.findAll();
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("products", products);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }

    @PostMapping("/delete")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    public ResponseEntity<?> delete(@RequestBody HashMap<String,ProductDelete> dataMap) throws RateLimiterException, AccountException {

        logger.info("ProductController delete method was called");

        validator.deleteProductValidate(dataMap, Arrays.asList("product"));

        ProductDelete product = dataMap.get("product");
        ProductDto productDto = productService.deleteProduct(product);
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("product", productDto);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }

    @PostMapping("/update")
    @RateLimit(limit = 20, duration = 60, unit = TimeUnit.SECONDS)
    public ResponseEntity<?> update(@RequestBody HashMap<String,ProductDto> dataMap) throws RateLimiterException, AccountException {

        logger.debug("ProductController update method was called");

        validator.updateProductValidate(dataMap, Arrays.asList("product"));

        ProductDto product = dataMap.get("product");
        ProductDto productDto = productService.updateProduct(product);
        Map<String, Object> resultOrders = new HashMap<>();
        resultOrders.put("product", productDto);
        return new ResponseEntity<>(resultOrders, HttpStatus.OK);
    }
}
