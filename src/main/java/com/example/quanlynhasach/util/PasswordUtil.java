package com.example.quanlynhasach.util;

import java.security.SecureRandom;

/**
 * Utility class for password operations
 */
public class PasswordUtil {
    
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom random = new SecureRandom();
    
    /**
     * Generate a temporary password with specified length
     * @param length Length of the password
     * @return Generated password
     */
    public static String generateTemporaryPassword(int length) {
        if (length < 4) {
            length = 8; // Minimum secure length
        }
        
        StringBuilder password = new StringBuilder(length);
        
        password.append(getRandomCharacter("ABCDEFGHIJKLMNOPQRSTUVWXYZ")); // Upper case
        password.append(getRandomCharacter("abcdefghijklmnopqrstuvwxyz")); // Lower case
        password.append(getRandomCharacter("0123456789")); // Digit
        password.append(getRandomCharacter("!@#$%^&*")); // Special character
        
        // Fill the rest with random characters
        for (int i = 4; i < length; i++) {
            password.append(getRandomCharacter(CHARACTERS));
        }
        
        // Shuffle the password to avoid predictable patterns
        return shuffleString(password.toString());
    }
    
    /**
     * Generate a temporary password with default length (8 characters)
     * @return Generated password
     */
    public static String generateTemporaryPassword() {
        return generateTemporaryPassword(8);
    }
    
    /**
     * Get a random character from the given character set
     */
    private static char getRandomCharacter(String characterSet) {
        return characterSet.charAt(random.nextInt(characterSet.length()));
    }
    
    /**
     * Shuffle the characters in a string
     */
    private static String shuffleString(String input) {
        char[] characters = input.toCharArray();
        
        // Fisher-Yates shuffle algorithm
        for (int i = characters.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = characters[i];
            characters[i] = characters[j];
            characters[j] = temp;
        }
        
        return new String(characters);
    }
}
