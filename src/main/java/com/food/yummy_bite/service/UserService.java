package com.food.yummy_bite.service;

import com.food.yummy_bite.dto.LoginRequest;
import com.food.yummy_bite.dto.LoginResponse;
import com.food.yummy_bite.dto.RegisterRequest;
import com.food.yummy_bite.dto.UserDto;
import com.food.yummy_bite.entity.User;
import com.food.yummy_bite.exception.BussinessException;
import com.food.yummy_bite.exception.DuplicateResourceException;
import com.food.yummy_bite.exception.ResourceNotFoundException;
import com.food.yummy_bite.repository.UserRepository;
import com.food.yummy_bite.util.PasswordUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordUtil passwordUtil;

    @Transactional
    public UserDto registerUser(RegisterRequest request){
        log.info("Registering new user {}",request.getEmail());
        //check if email is already existing
        if(userRepository.existsByEmail(request.getEmail())){
            throw new DuplicateResourceException("Email "+request.getEmail()+" already registered");
        }

        String hashedPassword = passwordUtil.hashPassword(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(hashedPassword)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phone(request.getPhone())
                .defaultLatitude(request.getDefaultLatitude())
                .defaultLongitude(request.getDefaultLongitude())
                .defaultAddress(request.getDefaultAddress())
                .isActive(true)
                .emailVerified(false)
                .build();
        User saved = userRepository.save(user);
        log.info("User registered successfully with ID:{}",saved.getId());
        return mapToDto(saved);
    }
    @Transactional(readOnly = true)
    public LoginResponse loginUser(LoginRequest request){
        log.info("Login attempt for user {}",request.getEmail());
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(()->new BussinessException("Invalid email or password"));
        if(!user.getIsActive()) throw new BussinessException("Account deactivated. Please contact support team");
        if(!passwordUtil.verifyPassword(request.getPassword() , passwordUtil.hashPassword(request.getPassword()))){
            throw new BussinessException("Invalid email or password");
        }
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        String token = generateSimpleToken(user);
        log.info("User logged in successfull: {}" , user.getEmail());
        return LoginResponse.builder()
                .token(token)
                .message("Login successfull")
                .user(mapToDto(user))
                .build();
    }

    @Transactional(readOnly = true)
    public UserDto getUserById(UUID id){
        log.info("Fetching user with id : {}" , id);
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User" , "ID" , id));
        return mapToDto(user);
    }
    @Transactional(readOnly = true)
    public UserDto getUserByEmail(String email){
        log.info("Fetching user with email : {}" , email);
        User user = userRepository.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("User" , "email" , email));
        return mapToDto(user);
    }
    @Transactional
    public UserDto updateUser(UUID id , RegisterRequest request){
        log.info("Updating user :{}" , id);
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User" , "Id" , id));
        if (!user.getEmail().equals(request.getEmail()) &&
                userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email '" + request.getEmail() + "' is already taken");
        }

        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setDefaultLatitude(request.getDefaultLatitude());
        user.setDefaultLongitude(request.getDefaultLongitude());
        user.setDefaultAddress(request.getDefaultAddress());

        User updated = userRepository.save(user);
        log.info("User updated successfully: {}" , updated.getId());
        return mapToDto(updated);
    }

    @Transactional
    public void deActivateUser(UUID id){
        log.info("Deactivating user :{}",id);
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User" , "ID" , id));
        user.setIsActive(false);
        userRepository.save(user);
        log.info("User deactivated :{}"+id);
    }

    @Transactional
    public void activateUser(UUID id){
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("User" , "ID" , id));
        user.setIsActive(true);
        userRepository.save(user);
        log.info("User activated : {}" , id);
    }


    private String generateSimpleToken(User user) {
        return "temp-token-"+user.getId()+"-"+System.currentTimeMillis();
    }


    private UserDto mapToDto(User user){
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .defaultLatitude(user.getDefaultLatitude())
                .defaultLongitude(user.getDefaultLongitude())
                .defaultAddress(user.getDefaultAddress())
                .isActive(user.getIsActive())
                .emailVerified(user.getEmailVerified())
                .lastLogin(user.getLastLogin())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
