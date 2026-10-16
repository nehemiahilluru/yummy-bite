package com.food.yummy_bite.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MenuItemDto {
    private UUID id;
    private UUID restaurantId;
    private String restaurantName;
    private String name;
    private String description;
    private BigDecimal price;
    private BigDecimal discountPrice;
    private String category;
    private Boolean isVegetarian;
    private Boolean isAvailable;
    private Integer preparationTime;
    private Integer calories;
    private String imageUrl;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
