package com.food.yummy_bite.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderItemRequest {
    @NotNull(message = "Menu item ID is required")
    private UUID menuItemId;
    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    private String specialInstructions;
}
