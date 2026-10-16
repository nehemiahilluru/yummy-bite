package com.food.yummy_bite.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponse {
    private String token;
    private UserDto user;
    private String message;
}
