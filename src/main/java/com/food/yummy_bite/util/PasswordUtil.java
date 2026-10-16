package com.food.yummy_bite.util;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class PasswordUtil {
    public String hashPassword(String plainPassword){
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hasing password " ,e);
        }
    }
    public boolean verifyPassword(String plainPassword , String hashedPassword){
        return hashPassword(plainPassword).equals(hashedPassword);
    }
}
