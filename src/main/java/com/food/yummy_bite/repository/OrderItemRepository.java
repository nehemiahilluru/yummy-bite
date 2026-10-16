package com.food.yummy_bite.repository;

import com.food.yummy_bite.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem , UUID> {

    List<OrderItem> findByOrderId(UUID orderId);

    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId AND oi.menuItem.id = :menuItemId")
    List<OrderItem> findByOrderIdAndMenuItemId(
            @Param("orderId") UUID orderId,
            @Param("menuItemId") UUID menuItemId
    );

    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.menuItem.id = :menuItemId")
    Integer getTotalQuantitySoldForMenuItem(@Param("menuItemId") UUID menuItemId);

    @Query("SELECT oi.menuItem.id, SUM(oi.quantity) as totalSold " +
            "FROM OrderItem oi " +
            "GROUP BY oi.menuItem.id " +
            "ORDER BY totalSold DESC " +
            "LIMIT :limit")
    List<Object[]> getMostPopularMenuItems(@Param("limit") int limit);
}
