package com.food.yummy_bite.controller;

import com.food.yummy_bite.dto.CreateMenuItemRequest;
import com.food.yummy_bite.dto.MenuItemDto;
import com.food.yummy_bite.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/restaurants/{restaurantId}/menu")
@RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService menuItemService;

    @PostMapping
    public ResponseEntity<MenuItemDto> addMenuItem(@PathVariable UUID restaurantId ,  @Valid @RequestBody CreateMenuItemRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(menuItemService.addMenuItem(restaurantId , request));
    }

    @GetMapping
    public ResponseEntity<List<MenuItemDto>> getmenuItems(@PathVariable UUID restaurantId){
        return ResponseEntity.ok(menuItemService.getMenuItemsByRestaurant(restaurantId));
    }

    @GetMapping("/available")
    public ResponseEntity<List<MenuItemDto>> getAvailableMenuItems(@PathVariable UUID restaurantId){
        return ResponseEntity.ok(menuItemService.getAvailableMenuItems(restaurantId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MenuItemDto>> searchMenuItem(@PathVariable UUID restaurantId , @RequestParam String keyword){
        return ResponseEntity.ok(menuItemService.searchByMenuItems(restaurantId , keyword));
    }

    @GetMapping("/{menuItemId}")
    public ResponseEntity<MenuItemDto> getMenuItem(@PathVariable UUID menuItemId){
        return ResponseEntity.ok(menuItemService.getMenuItem(menuItemId));
    }

    @PutMapping("/{menuItemId}")
    public ResponseEntity<MenuItemDto> updateMenuItem(@PathVariable UUID menuItemId ,@Valid @RequestBody CreateMenuItemRequest request){
        return ResponseEntity.ok(menuItemService.updateMenuItem(menuItemId , request));
    }

    @PatchMapping("/{menuItemId}/toggle-availability")
    public ResponseEntity<MenuItemDto> toggleMenuItemAvailability(@PathVariable UUID menuItemId){

        return ResponseEntity.ok(menuItemService.toggleAvailability(menuItemId));
    }

    @DeleteMapping("/{menuItemId}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable UUID menuItemId){
        menuItemService.deleteMenuItem(menuItemId);
        return ResponseEntity.noContent().build();
    }


}
