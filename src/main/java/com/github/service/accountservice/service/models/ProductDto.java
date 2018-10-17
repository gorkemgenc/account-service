package com.github.service.accountservice.service.models;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class ProductDto {

    private Integer id;
    private String name;
    private BigDecimal price;
    private Integer count;

    public ProductDto(){}

    public ProductDto(Integer id, String name, BigDecimal price, Integer count){
        this.id = id;
        this.name = name;
        this.price = price;
        this.count = count;
    }
}
