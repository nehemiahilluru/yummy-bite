package com.food.yummy_bite.service;

import com.food.yummy_bite.dto.CreateMenuItemRequest;
import com.food.yummy_bite.dto.MenuItemDto;
import com.food.yummy_bite.entity.MenuItem;
import com.food.yummy_bite.entity.Restaurant;
import com.food.yummy_bite.exception.BussinessException;
import com.food.yummy_bite.exception.ResourceNotFoundException;
import com.food.yummy_bite.repository.MenuItemRepository;
import com.food.yummy_bite.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuItemService {
    private final MenuItemRepository menuItemRepository;
    private final RestaurantRepository restaurantRepository;

    @Transactional
    public MenuItemDto addMenuItem(UUID restaurantId , CreateMenuItemRequest request){
        log.info("Adding new menu item to restaurant {}" , restaurantId);

        Restaurant restaurant =restaurantRepository.findById(restaurantId)
                .orElseThrow(()-> new ResourceNotFoundException("Restaurant" , "id" , restaurantId));

        List<MenuItem> existingItems = menuItemRepository.findByRestaurantId(restaurantId);
        if(existingItems.stream().anyMatch(item -> item.getName().equalsIgnoreCase(request.getName()))){
            throw  new BussinessException("Menu item already exists by name "+request.getName()+" in this restaurant");
        }
        MenuItem menuItem = MenuItem.builder()
                .restaurant(restaurant)
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .discountPrice(request.getDiscountPrice())
                .category(request.getCategory())
                .isVegetarian(request.getIsVegetarian()!=null?request.getIsVegetarian():false)
                .isAvailable(request.getIsAvailable()!=null?request.getIsAvailable():true)
                .preparationTime(request.getPreperationTime())
                .calories(request.getCalories())
                .imageUrl(request.getImageUrl())
                .sortOrder(request.getSortOrder())
                .build();
        MenuItem saved = menuItemRepository.save(menuItem);
        log.info("Menu item {} added into restaurant {}" , saved.getName() , restaurant.getName());
        return mapToDto(saved);
    }

    @Transactional(readOnly = true)
    public List<MenuItemDto> getMenuItemsByRestaurant(UUID restaurantId){
        log.debug("fetching menu items for restaurant: {}" , restaurantId);
        if(!restaurantRepository.existsById(restaurantId)){
            throw new ResourceNotFoundException("Restaurant", "id" , restaurantId);
        }

        return menuItemRepository.findByRestaurantId(restaurantId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<MenuItemDto> getAvailableMenuItems(UUID restaurantId){
        log.debug("Fetching available menu items for restaurant :{}",restaurantId);
        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MenuItemDto getMenuItem(UUID id){
        log.debug("Fetching menu item by id :{}" , id);
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("MenuItem" , "id" , id));
        return mapToDto(menuItem);
    }

    @Transactional
    public List<MenuItemDto> searchByMenuItems(UUID restaurantId , String keyword){
        log.debug("Searching menu items with keyword : {}" , keyword);

        if(!restaurantRepository.existsById(restaurantId)){
            throw new ResourceNotFoundException("restaurant" , "id" , restaurantId);
        }

        return menuItemRepository.searchMenuItems(restaurantId , keyword)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public MenuItemDto updateMenuItem(UUID id , CreateMenuItemRequest request){
        log.info("updating menu item : {}",id);
        MenuItem menuItem = menuItemRepository.findById(id)
                .orElseThrow(()->new ResourceNotFoundException("MenuItem" , "id" , id));
        List<MenuItem> exitingItems = menuItemRepository.findByRestaurantId(menuItem.getRestaurant().getId());
        if(exitingItems.stream().anyMatch(item -> !item.getId().equals(id) && !item.getName().equalsIgnoreCase(request.getName()))){
            throw new BussinessException("Menu item by name "+request.getName()+" already exisyts in this restaurant");
        }

        menuItem.setName(request.getName());
        menuItem.setDescription(request.getDescription());
        menuItem.setPrice(request.getPrice());
        menuItem.setDiscountPrice(request.getDiscountPrice());
        menuItem.setCategory(request.getCategory());
        menuItem.setIsVegetarian(request.getIsVegetarian());
        menuItem.setIsAvailable(request.getIsAvailable());
        menuItem.setPreparationTime(request.getPreperationTime());
        menuItem.setCalories(request.getCalories());
        menuItem.setImageUrl(request.getImageUrl());
        menuItem.setSortOrder(request.getSortOrder());

        MenuItem updated = menuItemRepository.save(menuItem);
        log.info("Menu item updated: {}",id);
        return mapToDto(updated);
    }

    @Transactional
    public void deleteMenuItem(UUID id){
        log.info("deleting menu item :{}" , id);
        if(!menuItemRepository.existsById(id)){
            throw new ResourceNotFoundException("MenuItem" , "id" , id);
        }
        menuItemRepository.deleteById(id);
        log.info("Menu item deleted : {}",id);
    }

    @Transactional
    public MenuItemDto toggleAvailability(UUID id){
        log.info("Toggling menu item availability :{}", id);
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("MenuItem" , "id" , id));
        menuItem.setIsAvailable(!menuItem.getIsAvailable());
        MenuItem   updated = menuItemRepository.save(menuItem);
        return mapToDto(updated);
    }

    private MenuItemDto mapToDto(MenuItem menuItem){
        return MenuItemDto.builder()
                .id(menuItem.getId())
                .restaurantId(menuItem.getRestaurant().getId())
                .restaurantName(menuItem.getRestaurant().getName())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .discountPrice(menuItem.getDiscountPrice())
                .category(menuItem.getCategory())
                .isVegetarian(menuItem.getIsVegetarian())
                .isAvailable(menuItem.getIsAvailable())
                .preparationTime(menuItem.getPreparationTime())
                .calories(menuItem.getCalories())
                .imageUrl(menuItem.getImageUrl())
                .sortOrder(menuItem.getSortOrder())
                .createdAt(menuItem.getCreatedAt())
                .updatedAt(menuItem.getUpdatedAt())
                .build();
    }

}
