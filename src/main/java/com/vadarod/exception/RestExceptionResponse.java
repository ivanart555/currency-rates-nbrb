package com.vadarod.exception;

import org.springframework.http.HttpStatus;

public class RestExceptionResponse extends RestResponse{

    private String exceptionName;

    private String exceptionMessage;

    public RestExceptionResponse(int status, HttpStatus statusMassage, String exceptionName, String exceptionMessage) {
        super();
        this.exceptionName = exceptionName;
        this.exceptionMessage = exceptionMessage;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public void setExceptionName(String exceptionName) {
        this.exceptionName = exceptionName;
    }

    public String getExceptionMessage() {
        return exceptionMessage;
    }

    public void setExceptionMessage(String exceptionMessage) {
        this.exceptionMessage = exceptionMessage;
    }
}
