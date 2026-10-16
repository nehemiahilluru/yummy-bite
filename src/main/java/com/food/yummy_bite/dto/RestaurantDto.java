package com.food.yummy_bite.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RestaurantDto{
    private UUID id;
    private String name;
    private String cuisineType;
    private Double longitute;
    private Double latitude;
    private String address;
    private String phone;
    private Integer averagePreperationTime;
    private BigDecimal deliveryFee;
    private BigDecimal minimumOrderAmount;
    private BigDecimal rating;
    private Integer totalReviews;
    private Boolean isOpen;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double distanceKm;
}
