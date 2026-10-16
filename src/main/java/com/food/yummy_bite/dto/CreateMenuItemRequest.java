package com.food.yummy_bite.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateMenuItemRequest {
    @NotBlank(message = "Name should not be blank")
    @Size(max = 100 ,message = "Name must be less than 100 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.01" , message = "Price must be greater than 0 ")
    private BigDecimal price;

    @DecimalMin(value = "0.01" , message = "Discount price cannot be negative")
    private BigDecimal discountPrice;

    private String category;

    private Boolean isVegetarian;

    private Boolean isAvailable;

    @Min(value = 1 , message = "Preperation time must be at least 1 minute")
    @Max(value = 120 , message = "Preperation time cannot exceed 120 minutes")
    private Integer preperationTime;

    @Min(value = 0 , message = "Calories cannot be negative")
    private Integer calories;

    private String imageUrl;

    private Integer sortOrder;
}
