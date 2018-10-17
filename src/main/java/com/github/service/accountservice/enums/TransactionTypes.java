package com.github.service.accountservice.enums;

public enum TransactionTypes {

    DEPOSIT(1),
    WITHDRAW(2),
    PURCHASE(3);

    private int code;

    TransactionTypes(int code){
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }

    public void setCode(int code){
        this.code = code;
    }
}
