package com.food.yummy_bite.repository;

import com.food.yummy_bite.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailAndIsActiveTrue(String email);
    List<User> findByIsActiveTrue();
    boolean existsByEmail(String email);
    @Query("SELECT u FROM User u WHERE u.lastLogin < :date AND u.isActive=true")
    List<User> findInActiveUsers(@Param("date") LocalDateTime data);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt > :date")
    long countNewUsersSince(@Param("date") LocalDateTime date);
}
