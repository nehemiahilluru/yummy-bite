package com.food.yummy_bite.repository;

import com.food.yummy_bite.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant , UUID> {

    Optional<Restaurant> findByName(String name);

    List<Restaurant> findByIsOpenTrue();

    @Query("SELECT COUNT(r) FROM Restaurant r where r.isOpen = true")
    long countOpenRestaurants();

    @Query("SELECT AVG(r.rating) FROM Restaurant r ")
    Double getAverageRatingAll();

    @Query(value = """
    SELECT r.*
    FROM (
        SELECT restaurants.*,
               6371 * ACOS(
                   COS(RADIANS(:lat)) *
                   COS(RADIANS(latitude)) *
                   COS(RADIANS(longitude) - RADIANS(:lng)) +
                   SIN(RADIANS(:lat)) *
                   SIN(RADIANS(latitude))
               ) AS distance_km
        FROM restaurants
        WHERE is_open = true
    ) r
    WHERE r.distance_km <= :radius
    ORDER BY r.distance_km ASC
    LIMIT 20
    """, nativeQuery = true)
    List<Restaurant> findNearByRestaurants(
            @Param("lat") double latitude,
            @Param("lng") double longitude,
            @Param("radius") double radiusKm
    );

}
