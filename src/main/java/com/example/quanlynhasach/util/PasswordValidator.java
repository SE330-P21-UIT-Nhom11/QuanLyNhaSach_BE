package com.example.quanlynhasach.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class PasswordValidator {

    private static final int MIN_LENGTH = 8;
    private static final int MAX_LENGTH = 128;
    
    // Regex patterns
    private static final Pattern UPPERCASE_PATTERN = Pattern.compile(".*[A-Z].*");
    private static final Pattern LOWERCASE_PATTERN = Pattern.compile(".*[a-z].*");
    private static final Pattern DIGIT_PATTERN = Pattern.compile(".*[0-9].*");
    private static final Pattern SPECIAL_CHAR_PATTERN = Pattern.compile(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");

    /**
     * Validate password với các quy tắc bảo mật
     */
    public ValidationResult validatePassword(String password) {
        ValidationResult result = new ValidationResult();

        if (password == null || password.isEmpty()) {
            result.addError("Password cannot be empty");
            return result;
        }

        // Check length
        if (password.length() < MIN_LENGTH) {
            result.addError("Password must be at least " + MIN_LENGTH + " characters long");
        }

        if (password.length() > MAX_LENGTH) {
            result.addError("Password must not exceed " + MAX_LENGTH + " characters");
        }

        // Check for uppercase letter
        if (!UPPERCASE_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one uppercase letter");
        }

        // Check for lowercase letter
        if (!LOWERCASE_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one lowercase letter");
        }

        // Check for digit
        if (!DIGIT_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one digit");
        }

        // Check for special character
        if (!SPECIAL_CHAR_PATTERN.matcher(password).matches()) {
            result.addError("Password must contain at least one special character (!@#$%^&*()_+-=[]{}|;':\"\\,./<>?)");
        }

        // Check for common weak passwords
        if (isCommonPassword(password)) {
            result.addError("Password is too common and easily guessable");
        }

        return result;
    }

    /**
     * Check if password is in common weak passwords list
     */
    private boolean isCommonPassword(String password) {
        String[] commonPasswords = {
            "password", "123456", "123456789", "12345678", "12345",
            "1234567", "admin", "qwerty", "abc123", "password123",
            "admin123", "root", "guest", "user", "test"
        };

        String lowerPassword = password.toLowerCase();
        for (String common : commonPasswords) {
            if (lowerPassword.contains(common)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get password strength score (0-100)
     */
    public int getPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }

        int score = 0;

        // Length bonus
        if (password.length() >= MIN_LENGTH) score += 25;
        if (password.length() >= 12) score += 15;
        if (password.length() >= 16) score += 10;

        // Character variety bonus
        if (UPPERCASE_PATTERN.matcher(password).matches()) score += 15;
        if (LOWERCASE_PATTERN.matcher(password).matches()) score += 15;
        if (DIGIT_PATTERN.matcher(password).matches()) score += 15;
        if (SPECIAL_CHAR_PATTERN.matcher(password).matches()) score += 15;

        // Penalty for common passwords
        if (isCommonPassword(password)) score -= 30;

        // Ensure score is between 0 and 100
        return Math.max(0, Math.min(100, score));
    }

    /**
     * Get password strength description
     */
    public String getPasswordStrengthDescription(int strength) {
        if (strength < 30) return "Weak";
        if (strength < 60) return "Fair";
        if (strength < 80) return "Good";
        return "Strong";
    }

    /**
     * Validation result class
     */
    public static class ValidationResult {
        private boolean valid = true;
        private StringBuilder errors = new StringBuilder();

        public void addError(String error) {
            valid = false;
            if (errors.length() > 0) {
                errors.append("; ");
            }
            errors.append(error);
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrors() {
            return errors.toString();
        }
    }
}
