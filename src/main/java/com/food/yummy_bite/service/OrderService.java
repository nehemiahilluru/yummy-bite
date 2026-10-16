package com.food.yummy_bite.service;

import com.food.yummy_bite.dto.OrderItemRequest;
import com.food.yummy_bite.dto.OrderItemResponse;
import com.food.yummy_bite.dto.OrderRequest;
import com.food.yummy_bite.dto.OrderResponse;
import com.food.yummy_bite.entity.*;
import com.food.yummy_bite.enums.OrderStatus;
import com.food.yummy_bite.enums.OrderType;
import com.food.yummy_bite.exception.ResourceNotFoundException;
import com.food.yummy_bite.exception.BussinessException;
import com.food.yummy_bite.repository.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    private static final BigDecimal TAX_RATE = new BigDecimal("0.08"); // 8% tax
    private static final BigDecimal MAX_DELIVERY_RADIUS_KM = new BigDecimal("10.0");

    // ==================== ORDER PLACEMENT ====================

    @Transactional
    public OrderResponse placeOrder(UUID userId, OrderRequest request) {
        log.info("Placing order for user: {} at restaurant: {}", userId, request.getRestaurantId());

        // 1. Validate User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

        if (!user.getIsActive()) {
            throw new BussinessException("User account is not active");
        }

        // 2. Validate Restaurant
        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant", "id", request.getRestaurantId()));

        if (!restaurant.getIsOpen()) {
            throw new BussinessException("Restaurant is currently closed");
        }

        // 3. Validate Order Type
        OrderType orderType;
        try {
            orderType = OrderType.valueOf(request.getOrderType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BussinessException("Invalid order type. Must be DELIVERY or PICKUP");
        }

        // 4. Validate Delivery Details (if DELIVERY)
        if (orderType == OrderType.DELIVERY) {
            validateDeliveryDetails(request, restaurant);
        }

        // 5. Validate and Build Order Items
        List<OrderItem> orderItems = buildOrderItems(request.getItems(), restaurant.getId());

        if (orderItems.isEmpty()) {
            throw new BussinessException("Order must contain at least one valid item");
        }

        // 6. Calculate Totals
        BigDecimal subtotal = orderItems.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal deliveryFee = orderType == OrderType.DELIVERY
                ? restaurant.getDeliveryFee() : BigDecimal.ZERO;

        BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);

        BigDecimal tipAmount = request.getTipAmount() != null
                ? request.getTipAmount() : BigDecimal.ZERO;

        // 7. Validate minimum order amount
        if (subtotal.compareTo(restaurant.getMinimumOrderAmount()) < 0) {
            throw new BussinessException(String.format(
                    "Minimum order amount is %.2f, but subtotal is %.2f",
                    restaurant.getMinimumOrderAmount(), subtotal
            ));
        }

        // 8. Create Order
        Order order = Order.builder()
                .user(user)
                .restaurant(restaurant)
                .orderReference(generateOrderReference())
                .orderStatus(OrderStatus.PENDING)
                .orderType(orderType)
                .subTotal(subtotal)
                .deliveryFee(deliveryFee)
                .tax(tax)
                .tipAmount(tipAmount)
                .discountApplied(BigDecimal.ZERO)
                .totalAmount(BigDecimal.ZERO) // Will be recalculated
                .deliveryAddress(orderType == OrderType.DELIVERY ?
                        request.getDeliveryAddress() : restaurant.getAddress())
                .deliveryLatitude(orderType == OrderType.DELIVERY ?
                        request.getDeliveryLatitude() : restaurant.getLatitude())
                .deliveryLongitude(orderType == OrderType.DELIVERY ?
                        request.getDeliveryLongitude() : restaurant.getLongitude())
                .deliveryInstructions(request.getDeliveryInstructions())
                .build();

        // Add items to order
        for (OrderItem item : orderItems) {
            order.addItem(item);
        }

        // Recalculate total (includes all fees)
        order.recalculateTotal();

        // 9. Calculate estimated delivery time
        order.setEstimatedDeliveryTime(calculateEstimatedDeliveryTime(restaurant, orderType));

        // 10. Save Order
        Order savedOrder = orderRepository.save(order);
        log.info("Order placed successfully: {} with reference: {}",
                savedOrder.getId(), savedOrder.getOrderReference());

        return mapToResponse(savedOrder);
    }

    // ==================== ORDER RETRIEVAL ====================

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {
        log.debug("Fetching order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByReference(String orderReference) {
        log.debug("Fetching order by reference: {}", orderReference);

        Order order = orderRepository.findByOrderReference(orderReference)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "reference", orderReference));

        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(UUID userId) {
        log.debug("Fetching orders for user: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User", "id", userId);
        }

        return orderRepository.findByUserId(userId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrdersByStatus(UUID userId, OrderStatus status) {
        log.debug("Fetching {} orders for user: {}", status, userId);

        return orderRepository.findByUserIdAndOrderStatus(userId, status).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getRestaurantOrders(UUID restaurantId) {
        log.debug("Fetching orders for restaurant: {}", restaurantId);

        if (!restaurantRepository.existsById(restaurantId)) {
            throw new ResourceNotFoundException("Restaurant", "id", restaurantId);
        }

        return orderRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getRecentOrdersByUser(UUID userId, int limit) {
        log.debug("Fetching recent {} orders for user: {}", limit, userId);

        return orderRepository.findRecentOrdersByUser(userId, (Pageable) PageRequest.of(0, limit))
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    // ==================== ORDER CANCELLATION ====================

    @Transactional
    public OrderResponse cancelOrder(UUID orderId, String reason) {
        log.info("Cancelling order: {} - Reason: {}", orderId, reason);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.isCancellable()) {
            throw new BussinessException(
                    "Order cannot be cancelled in status: " + order.getOrderStatus());
        }

        if (reason == null || reason.trim().isEmpty()) {
            throw new BussinessException("Cancellation reason is required");
        }

        order.setOrderStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(LocalDateTime.now());
        order.setCancellationReason(reason);
        order.setVersion(order.getVersion() + 1);

        Order cancelled = orderRepository.save(order);
        log.info("Order cancelled: {}", cancelled.getOrderReference());

        return mapToResponse(cancelled);
    }

    // ==================== HELPER METHODS ====================

    private void validateDeliveryDetails(OrderRequest request, Restaurant restaurant) {
        if (request.getDeliveryAddress() == null || request.getDeliveryAddress().trim().isEmpty()) {
            throw new BussinessException("Delivery address is required for delivery orders");
        }

        if (request.getDeliveryLatitude() == null || request.getDeliveryLongitude() == null) {
            throw new BussinessException("Delivery coordinates are required for delivery orders");
        }

        double distance = calculateHaversineDistance(
                restaurant.getLatitude(), restaurant.getLongitude(),
                request.getDeliveryLatitude(), request.getDeliveryLongitude()
        );

        if (distance > MAX_DELIVERY_RADIUS_KM.doubleValue()) {
            throw new BussinessException(String.format(
                    "Delivery address is %.2f km away. Maximum delivery radius is %.2f km",
                    distance, MAX_DELIVERY_RADIUS_KM
            ));
        }
    }

    private List<OrderItem> buildOrderItems(@NotEmpty(message = "At least one item is required") List<OrderItemRequest> itemRequests, UUID restaurantId) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : itemRequests) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item", "id", itemRequest.getMenuItemId()));

            // Verify item belongs to the restaurant
            if (!menuItem.getRestaurant().getId().equals(restaurantId)) {
                throw new BussinessException(
                        "Menu item '" + menuItem.getName() + "' does not belong to this restaurant"
                );
            }

            if (!menuItem.getIsAvailable()) {
                throw new BussinessException(
                        "Menu item '" + menuItem.getName() + "' is currently unavailable"
                );
            }

            // Use discounted price if available, otherwise regular price
            BigDecimal unitPrice = menuItem.getDiscountPrice() != null
                    ? menuItem.getDiscountPrice()
                    : menuItem.getPrice();

            BigDecimal totalPrice = unitPrice.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            ).setScale(2, RoundingMode.HALF_UP);

            OrderItem orderItem = OrderItem.builder()
                    .menuItem(menuItem)
                    .itemName(menuItem.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .totalPrice(totalPrice)
                    .specialInstructions(itemRequest.getSpecialInstructions())
                    .build();

            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private LocalDateTime calculateEstimatedDeliveryTime(Restaurant restaurant, OrderType orderType) {
        int prepTimeMinutes = restaurant.getAveragePreparationTime() != null
                ? restaurant.getAveragePreparationTime() : 15;

        int deliveryTimeMinutes = orderType == OrderType.DELIVERY ? 15 : 0;

        return LocalDateTime.now().plusMinutes(prepTimeMinutes + deliveryTimeMinutes);
    }

    private String generateOrderReference() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        return "ORD-" + timestamp + "-" + random;
    }

    private double calculateHaversineDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private OrderResponse mapToResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderReference(order.getOrderReference())
                .status(order.getOrderStatus())
                .orderType(order.getOrderType().name())
                .userId(order.getUser().getId())
                .userEmail(order.getUser().getEmail())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .subtotal(order.getSubTotal())
                .deliveryFee(order.getDeliveryFee())
                .tax(order.getTax())
                .tipAmount(order.getTipAmount())
                .discountApplied(order.getDiscountApplied())
                .totalAmount(order.getTotalAmount())
                .deliveryAddress(order.getDeliveryAddress())
                .deliveryInstructions(order.getDeliveryInstructions())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .createdAt(order.getCreatedAt())
                .items(order.getItems().stream()
                        .map(this::mapToItemResponse)
                        .collect(Collectors.toList()))
                .build();
    }

    private OrderItemResponse mapToItemResponse(OrderItem item) {
        return OrderItemResponse.builder()
                .id(item.getId())
                .itemName(item.getItemName())
                .quantity(item.getQuantity())
                .unitPrice(item.getUnitPrice())
                .totalPrice(item.getTotalPrice())
                .specialInstructions(item.getSpecialInstructions())
                .build();
    }


// ==================== ORDER STATUS TRANSITIONS ====================

    @Transactional
    public OrderResponse acceptOrder(UUID orderId) {
        log.info("Restaurant accepting order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() != OrderStatus.PENDING) {
            throw new BussinessException(
                    "Order cannot be accepted. Current status: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.ACCEPTED);
        order.setVersion(order.getVersion() + 1);

        Order updated = orderRepository.save(order);
        log.info("Order {} accepted", updated.getOrderReference());

        return mapToResponse(updated);
    }

    @Transactional
    public OrderResponse startPreparation(UUID orderId) {
        log.info("Restaurant starting preparation for order: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() != OrderStatus.ACCEPTED) {
            throw new BussinessException(
                    "Cannot start preparation. Current status: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.PREPARING);
        order.setPreparationStartedAt(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);

        Order updated = orderRepository.save(order);
        log.info("Order {} preparation started", updated.getOrderReference());

        return mapToResponse(updated);
    }

    @Transactional
    public OrderResponse markAsReady(UUID orderId) {
        log.info("Order marked as ready: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() != OrderStatus.PREPARING) {
            throw new BussinessException(
                    "Cannot mark as ready. Current status: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.READY);
        order.setReadyAt(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);

        Order updated = orderRepository.save(order);
        log.info("Order {} is ready for pickup", updated.getOrderReference());

        return mapToResponse(updated);
    }

    @Transactional
    public OrderResponse markAsPickedUp(UUID orderId) {
        log.info("Order picked up by driver: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() != OrderStatus.ASSIGNED) {
            throw new BussinessException(
                    "Cannot mark as picked up. Current status: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.PICKED_UP);
        order.setPickedUpAt(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);

        Order updated = orderRepository.save(order);
        log.info("Order {} picked up", updated.getOrderReference());

        return mapToResponse(updated);
    }

    @Transactional
    public OrderResponse markAsDelivered(UUID orderId) {
        log.info("Order delivered: {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (order.getOrderStatus() != OrderStatus.PICKED_UP) {
            throw new BussinessException(
                    "Cannot mark as delivered. Current status: " + order.getOrderStatus()
            );
        }

        order.setOrderStatus(OrderStatus.DELIVERED);
        order.setActualDeliveryTime(LocalDateTime.now());
        order.setVersion(order.getVersion() + 1);

        Order updated = orderRepository.save(order);
        log.info("Order {} delivered successfully", updated.getOrderReference());

        return mapToResponse(updated);
    }

    @Transactional
    public OrderResponse updateOrderStatus(UUID orderId, OrderStatus newStatus) {
        log.info("Updating order {} status to: {}", orderId, newStatus);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        if (!order.canTransitionTo(newStatus)) {
            throw new BussinessException(String.format(
                    "Invalid status transition from %s to %s",
                    order.getOrderStatus(), newStatus
            ));
        }

        order.setOrderStatus(newStatus);
        order.setVersion(order.getVersion() + 1);

        Order updated = orderRepository.save(order);
        return mapToResponse(updated);
    }
}
