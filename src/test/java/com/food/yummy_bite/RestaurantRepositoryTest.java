package com.food.yummy_bite;

import com.food.yummy_bite.entity.Restaurant;
import com.food.yummy_bite.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RestaurantRepositoryTest {
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Test
    void shouldSaveAndFindRestaurant(){
        Restaurant restaurant = Restaurant.builder()
                .name("Test Restaurant")
                .cuisineType("Italian")
                .latitude(40.7128)
                .longitude(12.1212)
                .address("123 abc ABC")
                .phone("+91 123445634")
                .build();
        //saved
        Restaurant saved =restaurantRepository.save(restaurant);
        //found
        Restaurant found = restaurantRepository.findById(saved.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("Test Restaurant");
    }
}
