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
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 100 , nullable = false , unique = true)
    private String email;

    @Column(length = 255 , nullable = false)
    private String password;

    @Column(name = "first_name", nullable = false , length = 50)
    private String firstName;

    @Column(name = "last_name", nullable = false , length = 50)
    private String lastName;

    @Column(nullable = false , length = 20)
    private String phone;

    @Column(name = "default_latitude" )
    private BigDecimal defaultLatitude;

    @Column(name = "default_longitude" )
    private BigDecimal defaultLongitude;

    @Column(name = "default_address" , columnDefinition = "TEXT")
    private String defaultAddress;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "email_verified")
    private Boolean emailVerified = false;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @CreationTimestamp
    @Column(name = "created_at" , updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
