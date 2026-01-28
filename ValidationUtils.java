package com.mm.quizmaster.utils;

import android.text.TextUtils;
import android.util.Patterns;
import com.mm.quizmaster.constants.AppConstants;

/**
 * Validation Utility Class
 * Handles all input validations
 */
public class ValidationUtils {
    
    /**
     * Validate email format
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    
    /**
     * Validate name
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= AppConstants.MIN_NAME_LENGTH;
    }
    
    /**
     * Validate password
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= AppConstants.MIN_PASSWORD_LENGTH;
    }
    
    /**
     * Validate question text
     */
    public static boolean isValidQuestion(String question) {
        return !TextUtils.isEmpty(question) && question.trim().length() >= AppConstants.MIN_QUESTION_LENGTH;
    }
    
    /**
     * Validate option text
     */
    public static boolean isValidOption(String option) {
        return !TextUtils.isEmpty(option) && option.trim().length() > 0;
    }
    
    /**
     * Check if passwords match
     */
    public static boolean doPasswordsMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
    
    /**
     * Get validation error message for name
     */
    public static String getNameError(String name) {
        if (TextUtils.isEmpty(name)) {
            return "Name is required";
        }
        if (name.trim().length() < AppConstants.MIN_NAME_LENGTH) {
            return "Name must be at least " + AppConstants.MIN_NAME_LENGTH + " characters";
        }
        return null;
    }
    
    /**
     * Get validation error message for email
     */
    public static String getEmailError(String email) {
        if (TextUtils.isEmpty(email)) {
            return "Email is required";
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return "Invalid email format";
        }
        return null;
    }
    
    /**
     * Get validation error message for password
     */
    public static String getPasswordError(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Password is required";
        }
        if (password.length() < AppConstants.MIN_PASSWORD_LENGTH) {
            return "Password must be at least " + AppConstants.MIN_PASSWORD_LENGTH + " characters";
        }
        return null;
    }
}
