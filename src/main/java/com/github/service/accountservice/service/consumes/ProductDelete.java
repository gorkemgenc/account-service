package com.github.service.accountservice.service.consumes;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductDelete {

    private Integer id;

    public ProductDelete(){}

    public ProductDelete(Integer id){
        this.id = id;
    }
}
