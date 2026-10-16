package com.food.yummy_bite.dto;

import com.food.yummy_bite.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrderStatusUpdateRequest   {
    @NotNull(message = "Status is required")
    private OrderStatus status;

    private String reason;
}
