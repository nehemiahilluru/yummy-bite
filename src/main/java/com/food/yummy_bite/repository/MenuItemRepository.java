package com.food.yummy_bite.repository;

import com.food.yummy_bite.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem , UUID> {

    List<MenuItem> findByRestaurantId(UUID restaurantId);

    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(UUID restaurantId);

    List<MenuItem> findByRestaurantIdAndCategory(UUID restaurantId , String category);

    @Query("SELECT m FROM MenuItem m WHERE m.id =:restaurantId AND m.price BETWEEN  :minPrice AND :maxPrice")
    List<MenuItem> findByRestaurantIdAndPriceRange(@Param("restaurantId") UUID restaurantId ,
                                                   @Param("minPrice")BigDecimal minPrice ,
                                                   @Param("maxPrice") BigDecimal maxPrice);

    @Query("SELECT m FROM MenuItem m WHERE m.restaurant.id =:restaurantId AND (LOWER(m.name) LIKE LOWER(CONCAT('%' , :keyword , '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword,'%')))")
    List<MenuItem> searchMenuItems(@Param("restaurantId") UUID restaurantId ,
                                   @Param("keyword") String keyword);

    long countByRestaurantId(UUID restaurantId);
}
