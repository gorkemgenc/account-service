package com.github.service.accountservice.enums;

public enum ErrorCode {

    BadRequest(400),
    NotFound(404),
    Conflict(409),
    Unprocessable_entity(422),
    Too_many_request(429);

    private int code;

    ErrorCode(int code){
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
