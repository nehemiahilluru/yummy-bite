package com.food.yummy_bite.enums;

public enum OrderStatus {
    PENDING("Order placed, waiting for restaurant"),
    ACCEPTED("Restarant accepted order") ,
    PREPARING("Restaurant is preparing food") ,
    READY("Food is ready for pickup"),
    ASSIGNED("Driver assined to pickup") ,
    PICKED_UP("Driver picked up the food") ,
    DELIVERED("Order delivered successfully") ,
    CANCELLED("Order cancelled");

    private final String description;

    OrderStatus(String description){
        this.description = description;
    }

    public String getDescription(){
        return description;
    }
    public boolean isTerminal(){
        return this==DELIVERED || this==CANCELLED;
    }
    public boolean isBefore(OrderStatus other){
        return this.ordinal() < other.ordinal();
    }
}
