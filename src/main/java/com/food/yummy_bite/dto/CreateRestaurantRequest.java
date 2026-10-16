package com.food.yummy_bite.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateRestaurantRequest {
    @NotBlank(message = "Restaurant name is required")
    @Size(max = 100 , message = "Name must be less than 100 characters")
    private String name;

    @Size(max = 50 ,message = "Cuisine type must be less than 50 characters")
    private String cuisineType;

    @NotNull(message = "Latitude is required")
    @DecimalMin(value = "-90.0" , message = "Latitude must be between -90 and 90")
    @DecimalMax(value = "90" , message = "Latitude must be between -90 and 90")
    private Double latitude;

    @NotNull(message = "Longitude is required")
    @DecimalMin(value = "-180.0" , message = "Longitude must be between -180 and 180")
    @DecimalMax(value = "180.0" , message = "Longitude must be between -180 and 180")
    private Double longitude;

    @NotBlank(message = "Address is required")
    private String address;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$" , message = "Invalid phone number format")
    private String phone;

    @Min(value = 5 , message = "Preparation time must be at least 5 minutes")
    @Max(value = 120 , message = "Preparetion time should not exceed 2 hours")
    private Integer averagePreparationTime;

    @DecimalMin(value = "0.0" , message = "Delivery fee cannot be negative")
    private BigDecimal deliveryFee;

    @DecimalMin(value = "0.0" , message = "Minimum order amount cannot be negative")
    private BigDecimal minimumOrderAmount;

    private String description;
    private String imageUrl;
}
