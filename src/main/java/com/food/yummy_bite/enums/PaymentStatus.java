package com.food.yummy_bite.enums;

public enum PaymentStatus {
    INITIATED("Payment initiated"),
    SUCCESS("Payment successfull"),
    FAILED("Payment failed") ,
    REFUNDED ("Payment refunded");

    private String description;

    PaymentStatus(String description){
        this.description=description;
    }

    public String getDescription(){
        return description;
    }
}
