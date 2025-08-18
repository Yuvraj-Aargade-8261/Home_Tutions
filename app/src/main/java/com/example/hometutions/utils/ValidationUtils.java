package com.example.hometutions.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.util.regex.Pattern;

public class ValidationUtils {
    
    // Email validation pattern
    private static final Pattern EMAIL_PATTERN = Patterns.EMAIL_ADDRESS;
    
    // Phone number validation pattern (Indian format)
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9]\\d{9}$");
    
    // Password validation pattern (minimum 8 characters, at least one letter and one number)
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*#?&]{8,}$");
    
    // Aadhar validation pattern (12 digits)
    private static final Pattern AADHAR_PATTERN = Pattern.compile("^\\d{12}$");
    
    // PAN validation pattern
    private static final Pattern PAN_PATTERN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]{1}$");
    
    /**
     * Validate email address
     */
    public static boolean isValidEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validate phone number (Indian format)
     */
    public static boolean isValidPhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validate password strength
     */
    public static boolean isValidPassword(String password) {
        if (TextUtils.isEmpty(password)) {
            return false;
        }
        return PASSWORD_PATTERN.matcher(password).matches();
    }
    
    /**
     * Validate Aadhar number
     */
    public static boolean isValidAadhar(String aadhar) {
        if (TextUtils.isEmpty(aadhar)) {
            return false;
        }
        return AADHAR_PATTERN.matcher(aadhar).matches();
    }
    
    /**
     * Validate PAN number
     */
    public static boolean isValidPAN(String pan) {
        if (TextUtils.isEmpty(pan)) {
            return false;
        }
        return PAN_PATTERN.matcher(pan).matches();
    }
    
    /**
     * Validate name (only letters and spaces)
     */
    public static boolean isValidName(String name) {
        if (TextUtils.isEmpty(name)) {
            return false;
        }
        // Allow letters, spaces, and common name characters
        return name.matches("^[a-zA-Z\\s\\-\\.']+$") && name.length() >= 2;
    }
    
    /**
     * Validate age
     */
    public static boolean isValidAge(String age) {
        if (TextUtils.isEmpty(age)) {
            return false;
        }
        try {
            int ageValue = Integer.parseInt(age);
            return ageValue >= 5 && ageValue <= 100;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate experience years
     */
    public static boolean isValidExperience(String experience) {
        if (TextUtils.isEmpty(experience)) {
            return false;
        }
        try {
            int expValue = Integer.parseInt(experience);
            return expValue >= 0 && expValue <= 50;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate budget range
     */
    public static boolean isValidBudget(String minBudget, String maxBudget) {
        if (TextUtils.isEmpty(minBudget) || TextUtils.isEmpty(maxBudget)) {
            return false;
        }
        
        try {
            int min = Integer.parseInt(minBudget);
            int max = Integer.parseInt(maxBudget);
            
            return min >= 0 && max >= 0 && min <= max && max <= 100000;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    /**
     * Validate address
     */
    public static boolean isValidAddress(String address) {
        if (TextUtils.isEmpty(address)) {
            return false;
        }
        return address.length() >= 10 && address.length() <= 200;
    }
    
    /**
     * Validate school/institution name
     */
    public static boolean isValidInstitutionName(String institution) {
        if (TextUtils.isEmpty(institution)) {
            return false;
        }
        return institution.length() >= 3 && institution.length() <= 100;
    }
    
    /**
     * Get password strength description
     */
    public static String getPasswordStrength(String password) {
        if (TextUtils.isEmpty(password)) {
            return "Password is required";
        }
        
        if (password.length() < 8) {
            return "Password must be at least 8 characters long";
        }
        
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*#?&].*");
        
        if (hasLetter && hasDigit && hasSpecial) {
            return "Strong password";
        } else if (hasLetter && hasDigit) {
            return "Good password";
        } else if (hasLetter || hasDigit) {
            return "Weak password";
        } else {
            return "Very weak password";
        }
    }
    
    /**
     * Get phone number format hint
     */
    public static String getPhoneFormatHint() {
        return "Enter 10-digit mobile number (e.g., 9876543210)";
    }
    
    /**
     * Get email format hint
     */
    public static String getEmailFormatHint() {
        return "Enter valid email address (e.g., user@example.com)";
    }
    
    /**
     * Get Aadhar format hint
     */
    public static String getAadharFormatHint() {
        return "Enter 12-digit Aadhar number";
    }
    
    /**
     * Get PAN format hint
     */
    public static String getPANFormatHint() {
        return "Enter PAN number (e.g., ABCDE1234F)";
    }
    
    /**
     * Sanitize input text (remove extra spaces, trim)
     */
    public static String sanitizeText(String text) {
        if (TextUtils.isEmpty(text)) {
            return "";
        }
        return text.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Validate file size (in bytes)
     */
    public static boolean isValidFileSize(long fileSize, long maxSize) {
        return fileSize > 0 && fileSize <= maxSize;
    }
    
    /**
     * Get file size in human readable format
     */
    public static String getFileSizeString(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        } else if (bytes < 1024 * 1024) {
            return String.format("%.1f KB", bytes / 1024.0);
        } else if (bytes < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
