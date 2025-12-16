package com.pagadasports.pagada.utils

import java.util.regex.Pattern

/**
 * SECURITY: Input sanitization utility to prevent injection attacks
 * 
 * While Supabase handles SQL injection protection at the backend level,
 * this provides additional client-side validation and sanitization.
 */
object InputSanitizer {
    
    // SECURITY: Whitelist of allowed characters for names (alphanumeric, spaces, hyphens, apostrophes)
    private val NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s'-]{1,100}$")
    
    // SECURITY: Email validation pattern (RFC 5322 simplified)
    private val EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    )
    
    // SECURITY: Detect potential SQL injection attempts
    private val SQL_INJECTION_PATTERN = Pattern.compile(
        "('|(\\-\\-)|(;)|(\\|\\|)|(\\/\\*)|(\\*\\/)|(xp_)|(sp_)|(exec)|(execute)|(select)|(insert)|(update)|(delete)|(drop)|(create)|(alter)|(union)|(script))",
        Pattern.CASE_INSENSITIVE
    )
    
    // SECURITY: Detect potential XSS attempts
    private val XSS_PATTERN = Pattern.compile(
        "(<script|<iframe|<object|<embed|javascript:|onerror=|onload=|onclick=)",
        Pattern.CASE_INSENSITIVE
    )
    
    /**
     * Sanitize name input - removes dangerous characters
     */
    fun sanitizeName(name: String): String {
        // Trim whitespace
        val trimmed = name.trim()
        
        // Remove any HTML/script tags
        val noHtml = trimmed.replace(Regex("<[^>]*>"), "")
        
        // Remove special characters that could be dangerous
        val sanitized = noHtml.replace(Regex("[<>\"';\\\\]"), "")
        
        // Limit length to prevent buffer overflow attacks
        return sanitized.take(100)
    }
    
    /**
     * Validate name format
     */
    fun isValidName(name: String): Boolean {
        if (name.isBlank()) return false
        if (name.length > 100) return false
        
        // Check against whitelist pattern
        if (!NAME_PATTERN.matcher(name).matches()) return false
        
        // Check for injection attempts
        if (containsSqlInjection(name)) return false
        if (containsXss(name)) return false
        
        return true
    }
    
    /**
     * Sanitize email input
     */
    fun sanitizeEmail(email: String): String {
        // Trim and lowercase
        val sanitized = email.trim().lowercase()
        
        // Remove any spaces
        return sanitized.replace("\\s".toRegex(), "")
    }
    
    /**
     * Validate email format
     */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        if (email.length > 254) return false // RFC 5321
        
        // Check against email pattern
        if (!EMAIL_PATTERN.matcher(email).matches()) return false
        
        // Additional Android validation
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) return false
        
        // Check for injection attempts
        if (containsSqlInjection(email)) return false
        
        return true
    }
    
    /**
     * Validate password strength
     */
    fun validatePasswordStrength(password: String): PasswordValidationResult {
        val errors = mutableListOf<String>()
        
        if (password.length < 8) {
            errors.add("be at least 8 characters")
        }
        if (password.length > 128) {
            errors.add("be less than 128 characters")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("contain a number")
        }
        if (!password.any { it.isUpperCase() }) {
            errors.add("contain an uppercase letter")
        }
        if (!password.any { it.isLowerCase() }) {
            errors.add("contain a lowercase letter")
        }
        if (password.all { it.isLetterOrDigit() }) {
            errors.add("contain a special character")
        }
        
        // SECURITY: Check for common weak passwords
        if (isCommonPassword(password)) {
            errors.add("not be a common password")
        }
        
        return PasswordValidationResult(errors.isEmpty(), errors)
    }
    
    /**
     * Check if password is in common password list
     */
    private fun isCommonPassword(password: String): Boolean {
        val commonPasswords = setOf(
            "password", "12345678", "password123", "admin123",
            "qwerty123", "letmein", "welcome", "monkey",
            "1234567890", "password1", "abc123", "admin"
        )
        return commonPasswords.contains(password.lowercase())
    }
    
    /**
     * Detect potential SQL injection attempts
     */
    private fun containsSqlInjection(input: String): Boolean {
        return SQL_INJECTION_PATTERN.matcher(input).find()
    }
    
    /**
     * Detect potential XSS attempts
     */
    private fun containsXss(input: String): Boolean {
        return XSS_PATTERN.matcher(input).find()
    }
    
    /**
     * Rate limiting helper - check if action is too frequent
     */
    fun isRateLimited(lastActionTime: Long, minIntervalMs: Long = 1000): Boolean {
        val currentTime = System.currentTimeMillis()
        return (currentTime - lastActionTime) < minIntervalMs
    }
    
    /**
     * Sanitize general text input
     */
    fun sanitizeText(text: String, maxLength: Int = 1000): String {
        // Trim whitespace
        val trimmed = text.trim()
        
        // Remove any HTML/script tags
        val noHtml = trimmed.replace(Regex("<[^>]*>"), "")
        
        // Remove null bytes (could be used for injection)
        val noNullBytes = noHtml.replace("\u0000", "")
        
        // Limit length
        return noNullBytes.take(maxLength)
    }
}

/**
 * Result of password validation
 */
data class PasswordValidationResult(
    val isValid: Boolean,
    val errors: List<String>
)
