package com.food.yummy_bite.repository;

import com.food.yummy_bite.entity.Order;
import com.food.yummy_bite.enums.OrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order , UUID> {


    Optional<Order> findByOrderReference(String orderReference);

    List<Order> findByUserId(UUID userId);

    List<Order> findByUserIdAndOrderStatus(UUID userId, OrderStatus status);

    List<Order> findByRestaurantId(UUID restaurantId);

    List<Order> findByRestaurantIdAndOrderStatus(UUID restaurantId, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    List<Order> findRecentOrdersByUser(@Param("userId") UUID userId, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.orderStatus IN :statuses")
    List<Order> findByStatuses(@Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(o) FROM Order o WHERE o.createdAt > :date AND o.orderStatus = :status")
    long countOrdersSinceWithStatus(
            @Param("date") LocalDateTime date,
            @Param("status") OrderStatus status
    );

    @Query("SELECT o.restaurant.id, COUNT(o) as orderCount " +
            "FROM Order o " +
            "WHERE o.createdAt > :date " +
            "GROUP BY o.restaurant.id " +
            "ORDER BY orderCount DESC")
    List<Object[]> getTopRestaurantsByOrders(@Param("date") LocalDateTime date, Pageable pageable);

    @Query("SELECT DATE(o.createdAt), COUNT(o) " +
            "FROM Order o " +
            "WHERE o.createdAt > :date " +
            "GROUP BY DATE(o.createdAt) " +
            "ORDER BY DATE(o.createdAt) DESC")
    List<Object[]> getOrderCountByDay(@Param("date") LocalDateTime date);

    @Query("SELECT AVG(o.totalAmount) FROM Order o WHERE o.createdAt > :date AND o.orderStatus = 'DELIVERED'")
    Double getAverageOrderValue(@Param("date") LocalDateTime date);

    @Modifying
    @Query("UPDATE Order o SET o.orderStatus = :status, o.cancelledAt = :cancelledAt, " +
            "o.cancellationReason = :reason WHERE o.id = :orderId")
    void updateOrderStatusToCancelled(
            @Param("orderId") UUID orderId,
            @Param("status") OrderStatus status,
            @Param("cancelledAt") LocalDateTime cancelledAt,
            @Param("reason") String reason
    );

    @Query("SELECT o FROM Order o WHERE o.orderStatus IN ('PENDING', 'ACCEPTED') " +
            "AND o.createdAt < :timeout")
    List<Order> findStalePendingOrders(@Param("timeout") LocalDateTime timeout);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.restaurant.id = :restaurantId " +
            "AND o.orderStatus IN ('PENDING', 'ACCEPTED', 'PREPARING', 'READY')")
    long countActiveOrdersForRestaurant(@Param("restaurantId") UUID restaurantId);
}
