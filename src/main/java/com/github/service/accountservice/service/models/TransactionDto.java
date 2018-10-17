package com.github.service.accountservice.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
public class TransactionDto {

    private long id;
    private Integer accountId;
    private BigDecimal amount;
    private Date date;
    private String type;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer productId;

    public TransactionDto(){
        this.date = new Date();
    }
}
