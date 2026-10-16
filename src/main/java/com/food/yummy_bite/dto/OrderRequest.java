package com.food.yummy_bite.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class OrderRequest {

    @NotNull(message = "Restaurant ID is required")
    private UUID restaurantId;

    @NotNull(message = "Order type is required")
    private String orderType; // DELIVERY or PICKUP

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<OrderItemRequest> items;

    @Size(max = 500, message = "Delivery instructions cannot exceed 500 characters")
    private String deliveryInstructions;

    private BigDecimal tipAmount;

    // Delivery fields (required for DELIVERY)
    private String deliveryAddress;
    private Double deliveryLatitude;
    private Double deliveryLongitude;
}
