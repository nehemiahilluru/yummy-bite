package com.food.yummy_bite.exception;

public class BussinessException extends  RuntimeException{

    public final String errorCode;
    public BussinessException(String  msg){
        super(msg);
        this.errorCode = "BUSSINESS_ERROR";
    }

    public BussinessException(String msg , String errorCode){
        super(msg);
        this.errorCode=errorCode;
    }
    public String getErrorCode(){
        return errorCode;
    }


}
