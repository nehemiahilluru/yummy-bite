package com.food.yummy_bite.controller;

import com.food.yummy_bite.dto.LoginRequest;
import com.food.yummy_bite.dto.LoginResponse;
import com.food.yummy_bite.dto.RegisterRequest;
import com.food.yummy_bite.dto.UserDto;
import com.food.yummy_bite.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.geolatte.geom.V;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDto>  register(@Valid @RequestBody RegisterRequest request){
        UserDto response = userService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request){
        LoginResponse response = userService.loginUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable UUID id){
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDto> getUserByEmail(@PathVariable String email){
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id ,@Valid @RequestBody RegisterRequest request){
        return ResponseEntity.ok(userService.updateUser(id , request));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deActivateUser(@PathVariable UUID id){
        userService.deActivateUser(id);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activateUser(@PathVariable UUID id){
        userService.activateUser(id);
        return ResponseEntity.noContent().build();
    }
}
