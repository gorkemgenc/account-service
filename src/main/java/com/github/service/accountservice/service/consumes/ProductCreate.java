package com.github.service.accountservice.service.consumes;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductCreate {

    private String name;
    private BigDecimal price;
    private Integer count;

    public ProductCreate(){}

    public ProductCreate(String name, BigDecimal price, Integer count){
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
