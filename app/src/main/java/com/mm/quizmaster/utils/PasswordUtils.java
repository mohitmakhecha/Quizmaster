package com.mm.quizmaster.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Password Utility Class
 * Handles password hashing using SHA-256
 */
public class PasswordUtils {
    
    /**
     * Hash password using SHA-256
     * @param password Plain text password
     * @return Hashed password as hex string
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback to plain text if hashing fails
        }
    }
    
    /**
     * Verify password against hash
     * @param password Plain text password
     * @param hashedPassword Hashed password to compare
     * @return true if passwords match
     */
    public static boolean verifyPassword(String password, String hashedPassword) {
        return hashPassword(password).equals(hashedPassword);
    }
}
