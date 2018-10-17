package com.github.service.accountservice.enums;

public enum ErrorMessage {

    RATE_LIMITER_FLOW("Too many requests within short period. Please wait and try again."),
    REQUEST_NOT_FOUND("Request not found."),
    BAD_REQUEST_FORMAT_ERROR("Parameters format is invalid"),
    CREATE_TRANSACTION_ERROR("Create Transaction Error"),
    SHOULD_GREATER_THAN_ZERO("%s should be greater than zero"),
    NO_ENOUGH_BALANCE("There is no enough balance"),
    ACCOUNT_NOT_FOUND_EXCEPTION("Account not found exception"),
    ACCOUNT_NOT_CREATED_EXCEPTION("Account couldn't be created"),
    PRODUCT_NOT_CREATED_EXCEPTION("Product couldn't be created"),
    NUMBER_FORMAT_MISMATCH("%s should be a number"),
    NAME_SHOULD_BE_FILLED("Name should be filled"),
    PRODUCT_IS_NOT_VALID("Product is not valid"),
    DELETE_PRODUCT_METHOD_NOT_WORKED("Delete product method didn't work"),
    SHOULD_NOT_BE_SMALLER_THAN_ZERO("%s should not be smaller than zero"),
    METHOD_NOT_WORKED("Method didn't completed successfully"),
    CURRENT_BALANCE_SHOULD_BE_GREATER_THAN_PRICE("Current balance should be greater than product price"),
    TRANSACTION_NOT_CREATED_EXCEPTION("Transaction couldn't be created"),
    NO_MANDATORY_FIELD("Mandatory field is missing"),
    NAME_SHOULD_BE_DIFFERENCE("Name should be unique");

    private String message;

    ErrorMessage(String message){
        this.message = message;
    }

    public void setMessage(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }
}
