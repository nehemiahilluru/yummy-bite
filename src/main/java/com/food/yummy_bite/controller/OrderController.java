package com.food.yummy_bite.controller;

import com.food.yummy_bite.dto.OrderRequest;
import com.food.yummy_bite.dto.OrderResponse;
import com.food.yummy_bite.dto.OrderStatusUpdateRequest;
import com.food.yummy_bite.enums.OrderStatus;
import com.food.yummy_bite.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;

    // ==================== PLACE ORDER ====================

    @PostMapping("/user/{userId}")
    public ResponseEntity<OrderResponse> placeOrder(
            @PathVariable UUID userId,
            @Valid @RequestBody OrderRequest request) {

        OrderResponse order = orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(order);
    }

    // ==================== GET ORDER ====================

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @GetMapping("/reference/{orderReference}")
    public ResponseEntity<OrderResponse> getOrderByReference(
            @PathVariable String orderReference) {
        return ResponseEntity.ok(orderService.getOrderByReference(orderReference));
    }

    // ==================== USER ORDERS ====================

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getUserOrders(@PathVariable UUID userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    @GetMapping("/user/{userId}/status/{status}")
    public ResponseEntity<List<OrderResponse>> getUserOrdersByStatus(
            @PathVariable UUID userId,
            @PathVariable OrderStatus status) {
        return ResponseEntity.ok(orderService.getUserOrdersByStatus(userId, status));
    }

    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<OrderResponse>> getRecentOrders(
            @PathVariable UUID userId,
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(orderService.getRecentOrdersByUser(userId, limit));
    }

    // ==================== RESTAURANT ORDERS ====================

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(
            @PathVariable UUID restaurantId) {
        return ResponseEntity.ok(orderService.getRestaurantOrders(restaurantId));
    }

    // ==================== CANCEL ORDER ====================

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable UUID orderId,
            @RequestParam String reason) {
        return ResponseEntity.ok(orderService.cancelOrder(orderId, reason));
    }

    // ==================== STATUS TRANSITIONS ====================

    @PatchMapping("/{orderId}/accept")
    public ResponseEntity<OrderResponse> acceptOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.acceptOrder(orderId));
    }

    @PatchMapping("/{orderId}/start-preparation")
    public ResponseEntity<OrderResponse> startPreparation(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.startPreparation(orderId));
    }

    @PatchMapping("/{orderId}/ready")
    public ResponseEntity<OrderResponse> markAsReady(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.markAsReady(orderId));
    }

    @PatchMapping("/{orderId}/picked-up")
    public ResponseEntity<OrderResponse> markAsPickedUp(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.markAsPickedUp(orderId));
    }

    @PatchMapping("/{orderId}/delivered")
    public ResponseEntity<OrderResponse> markAsDelivered(@PathVariable UUID orderId) {
        return ResponseEntity.ok(orderService.markAsDelivered(orderId));
    }

    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable UUID orderId,
            @RequestBody OrderStatusUpdateRequest request) {
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, request.getStatus()));
    }
}
