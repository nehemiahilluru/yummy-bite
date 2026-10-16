package com.food.yummy_bite.controller;

import com.food.yummy_bite.dto.CreateRestaurantRequest;
import com.food.yummy_bite.dto.RestaurantDto;
import com.food.yummy_bite.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<RestaurantDto> createRestaurant(@Valid @RequestBody CreateRestaurantRequest request){
        RestaurantDto created = restaurantService.createRestaurant(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<RestaurantDto>> getAllRestaurants(){
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantDto> getRestaurantById(@PathVariable UUID id){
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<RestaurantDto>> getNearByRestaurant(@RequestParam double latitude , @RequestParam double longitude , @RequestParam(defaultValue = "10") double radiusKm ){
        return ResponseEntity.ok(restaurantService.getNearByRestaurant(latitude,longitude,radiusKm));
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantDto> updateRestaurant(@PathVariable UUID id , @Valid @RequestBody CreateRestaurantRequest request ){
        return ResponseEntity.ok(restaurantService.updateRestaurant(id , request));
    }
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<RestaurantDto> toggleStatus(@PathVariable UUID id){
        return ResponseEntity.ok(restaurantService.toggleRestaurantStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRestaurant(@PathVariable UUID id){
        restaurantService.deleteRestaurant(id);
        return  ResponseEntity.noContent().build();
    }
}
