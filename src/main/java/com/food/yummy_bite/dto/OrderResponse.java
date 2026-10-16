package com.food.yummy_bite.dto;

import com.food.yummy_bite.enums.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class OrderResponse {
    private UUID id;
    private String orderReference;
    private OrderStatus status;
    private String orderType;
    private UUID userId;
    private String userEmail;
    private UUID restaurantId;
    private String restaurantName;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal tax;
    private BigDecimal tipAmount;
    private BigDecimal discountApplied;
    private BigDecimal totalAmount;
    private String deliveryAddress;
    private String deliveryInstructions;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items;

}
