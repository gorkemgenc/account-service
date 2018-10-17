package com.github.service.accountservice.service.models;

import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class AccountDto {

    private Integer id;
    private BigDecimal balance;

    public AccountDto(){}

    public AccountDto(Integer id, BigDecimal balance){
        this.id = id;
        this.balance = balance;
    }
}
