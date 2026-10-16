package com.food.yummy_bite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 8 , message = "Password must be atleast 8 characters")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).+$" ,
            message = "Password must contain at least one uppercase letter, one lowercase letter, and one digit")
    private String password;

    @NotBlank(message = "Firstname is required")
    @Size(max = 50 , message = "Firstname must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50 , message = "Last name should not exceed 50 characters")
    private String lastName;

    @Pattern(regexp = "^\\+?[0-9]{10,15}$" , message = "Invalid phone number format")
    private String phone;

    private BigDecimal defaultLatitude;
    private BigDecimal defaultLongitude;
    private String defaultAddress;
}
