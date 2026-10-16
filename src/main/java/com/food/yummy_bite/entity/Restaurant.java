package com.food.yummy_bite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "restaurants")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false , length = 100)
    private String name;

    @Column(length = 50)
    private String cuisineType;

    @Column(nullable = false )
    private Double longitude;

    @Column(nullable = false )
    private Double latitude;

    @Column(nullable = false ,columnDefinition = "TEXT")
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "average_preparation_time")
    private Integer averagePreparationTime = 15;

    @Column(name = "delivery_fee" , precision = 10 , scale = 2)
    private BigDecimal deliveryFee = BigDecimal.ZERO;

    @Column(name = "minimum_order_amount" , precision = 10, scale = 2)
    private BigDecimal minimumOrderAmount = BigDecimal.ZERO;

    @Column(precision = 3 , scale = 2)
    private BigDecimal rating =BigDecimal.ZERO;

    @Column(name = "total_reviews")
    private Integer totalReviews = 0;

    @Column(name = "is_open")
    private Boolean isOpen = true;

    @Column(name = "description" , columnDefinition = "TEXT")
    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" )
    private LocalDateTime updatedAt;
}
