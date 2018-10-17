package com.github.service.accountservice.exceptions;

public class AccountException extends Exception{
    private int errorCode;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public AccountException(int errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public AccountException(){
        super();

    }

    public AccountException(String message){
        super(message);
    }

    public AccountException(Exception e){
        super(e);
    }


}
