package com.food.yummy_bite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.lang.instrument.ClassDefinition;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id" , nullable = false)
    private Restaurant restaurant;

    @Column(nullable = false , length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false , precision = 10 , scale = 2)
    private BigDecimal price;

    @Column(name = "discount_price" , precision = 10 , scale = 2)
    private BigDecimal discountPrice;

    @Column(length = 50)
    private String category;

    @Column(name = "is_vegetarian")
    private Boolean isVegetarian = false;

    @Column(name = "is_available")
    private Boolean isAvailable = true;

    @Column(name = "preparation_time")
    private Integer preparationTime;

    private Integer calories;

    @Column(name = "image_url" , length = 255)
    private String imageUrl;

    @Column(name = "sort_order")
    private Integer sortOrder=0;

    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at" )
    private LocalDateTime updatedAt;
}
