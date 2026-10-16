package com.food.yummy_bite.enums;

public enum OrderType {
    DELIVERY("Delivery to customer"),
    PICK_UP("Pick up by customer");
    private final String description;
    OrderType(String description){
        this.description = description;
    }
    public String getDescription(){
        return description;
    }
}
