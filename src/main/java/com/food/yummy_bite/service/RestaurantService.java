package com.food.yummy_bite.service;

import com.food.yummy_bite.dto.CreateRestaurantRequest;
import com.food.yummy_bite.dto.RestaurantDto;
import com.food.yummy_bite.entity.Restaurant;
import com.food.yummy_bite.exception.DuplicateResourceException;
import com.food.yummy_bite.exception.ResourceNotFoundException;
import com.food.yummy_bite.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public RestaurantDto createRestaurant(CreateRestaurantRequest request){
        log.info("Creating new restaurant {}",request.getName());

        //checking for duplicates
        restaurantRepository.findByName(request.getName())
                .ifPresent( r -> {
                    throw  new DuplicateResourceException("Restaurant already exists by name "+r.getName());
                });
        //creating new restaurant
        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .cuisineType(request.getCuisineType())
                .phone(request.getPhone())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(request.getAddress())
                .averagePreparationTime(request.getAveragePreparationTime()!=null?request.getAveragePreparationTime():15)
                .deliveryFee(request.getDeliveryFee()!=null?request.getDeliveryFee(): BigDecimal.ZERO)
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .minimumOrderAmount(request.getMinimumOrderAmount()!=null?request.getMinimumOrderAmount():BigDecimal.ZERO)
                .isOpen(true)
                .rating(BigDecimal.ZERO)
                .totalReviews(0)
                .build();
        Restaurant saved = restaurantRepository.save(restaurant);
        return mapToDto(saved);
    }
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double R = 6371; // Earth's radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon/2) * Math.sin(dLon/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return R * c;
    }

    @Transactional(readOnly = true)
    public List<RestaurantDto> getAllRestaurants(){
        log.debug("Fetching all restaurants");
        return restaurantRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RestaurantDto getRestaurantById(UUID id){
        log.debug("fetching reataurant by ID : {}", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Restaurant","id",id));
        return mapToDto(restaurant);
    }

    @Transactional(readOnly = true)
    public List<RestaurantDto> getNearByRestaurant(double latitude, double longitude, double radiusKm) {
        log.debug(
                "Finding nearby restaurants within {} km of ({}, {})",
                radiusKm, latitude, longitude
        );

        List<Restaurant> restaurants =
                restaurantRepository.findNearByRestaurants(latitude, longitude, radiusKm);

        return restaurants.stream()
                .map(r -> {
                    RestaurantDto dto = mapToDto(r);
                    dto.setDistanceKm(
                            calculateDistance(
                                    latitude,
                                    longitude,
                                    r.getLatitude(),
                                    r.getLongitude()
                            )
                    );
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public RestaurantDto updateRestaurant(UUID id , CreateRestaurantRequest request){
        log.info("Updating restaurant :{}", id);

        Restaurant restaurant = restaurantRepository.findById(id).orElseThrow(()->new ResourceNotFoundException("Restaurant","id",id));

        //checkig duplicate name
        restaurantRepository.findByName(request.getName()).ifPresent(r->{
            if(!r.getId().equals(id))
            {
                throw  new DuplicateResourceException("restaurant already exists by name "+request.getName());
            }
        });

        restaurant.setName(request.getName());
        restaurant.setCuisineType(request.getCuisineType());
        restaurant.setLatitude(request.getLatitude());
        restaurant.setLongitude(request.getLongitude());
        restaurant.setAddress(request.getAddress());
        restaurant.setPhone(request.getPhone());
        restaurant.setAveragePreparationTime(request.getAveragePreparationTime());
        restaurant.setDeliveryFee(request.getDeliveryFee());
        restaurant.setMinimumOrderAmount(request.getMinimumOrderAmount());
        restaurant.setDescription(request.getDescription());
        restaurant.setImageUrl(request.getImageUrl());

        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurant updated {}",updated.getId());
        return mapToDto(updated);
    }

    @Transactional
    public void deleteRestaurant(UUID id){
        log.info("Deleting restaurant {}",id);
        if(!restaurantRepository.existsById(id)){
            throw new ResourceNotFoundException("Restaurant" ,"id",id);
        }
        restaurantRepository.deleteById(id);
        log.info("Restaurant deleted :{}",id);
    }

    @Transactional
    public RestaurantDto toggleRestaurantStatus(UUID id){
        log.info("Toggling restaurant status: {}",id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Restaurant" , "id" , id));
        restaurant.setIsOpen(!restaurant.getIsOpen());
        Restaurant updated = restaurantRepository.save(restaurant);
        log.info("Restaurant {} status toggled to:{}",id , updated.getIsOpen());
        return mapToDto(updated);
    }

    public RestaurantDto mapToDto(Restaurant restaurant){
        return RestaurantDto.builder()
                .id(restaurant.getId())
                .address(restaurant.getAddress())
                .averagePreperationTime(restaurant.getAveragePreparationTime())
                .createdAt(restaurant.getCreatedAt())
                .cuisineType(restaurant.getCuisineType())
                .deliveryFee(restaurant.getDeliveryFee())
                .description(restaurant.getDescription())
                .imageUrl(restaurant.getImageUrl())
                .isOpen(restaurant.getIsOpen())
                .latitude(restaurant.getLatitude())
                .longitute(restaurant.getLongitude())
                .minimumOrderAmount(restaurant.getMinimumOrderAmount())
                .name(restaurant.getName())
                .phone(restaurant.getPhone())
                .rating(restaurant.getRating())
                .totalReviews(restaurant.getTotalReviews())
                .updatedAt(restaurant.getUpdatedAt())
                .build();
    }
}
