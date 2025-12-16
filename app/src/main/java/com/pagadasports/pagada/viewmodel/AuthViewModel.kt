package com.pagadasports.pagada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagadasports.pagada.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val error: String? = null,
    val user: io.github.jan.supabase.gotrue.user.UserInfo? = null
)

class AuthViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    // SECURITY: Rate limiting to prevent brute force attacks
    private var lastLoginAttempt: Long = 0
    private var lastSignUpAttempt: Long = 0
    private var failedLoginAttempts = 0
    private val MAX_FAILED_ATTEMPTS = 5
    private val RATE_LIMIT_MS = 2000L // 2 seconds between attempts
    private val LOCKOUT_DURATION_MS = 300000L // 5 minutes lockout after max attempts

    init {
        checkAuthStatus()
    }

    private fun checkAuthStatus() {
        viewModelScope.launch {
            try {
                val session = supabase.auth.currentSessionOrNull()
                _authState.value = _authState.value.copy(
                    isAuthenticated = session != null,
                    user = session?.user
                )
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    error = e.message
                )
            }
        }
    }

    fun signIn(email: String, password: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            // SECURITY: Check if account is locked due to too many failed attempts
            val currentTime = System.currentTimeMillis()
            if (failedLoginAttempts >= MAX_FAILED_ATTEMPTS) {
                val timeSinceLastAttempt = currentTime - lastLoginAttempt
                if (timeSinceLastAttempt < LOCKOUT_DURATION_MS) {
                    val remainingMinutes = ((LOCKOUT_DURATION_MS - timeSinceLastAttempt) / 60000) + 1
                    _authState.value = _authState.value.copy(
                        isLoading = false,
                        error = "Too many failed attempts. Please try again in $remainingMinutes minutes."
                    )
                    return@launch
                } else {
                    // Reset after lockout period
                    failedLoginAttempts = 0
                }
            }
            
            // SECURITY: Rate limiting
            if (currentTime - lastLoginAttempt < RATE_LIMIT_MS) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Please wait before trying again"
                )
                return@launch
            }
            
            lastLoginAttempt = currentTime
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            try {
                supabase.auth.signInWith(Email) {
                    this.email = email.trim().lowercase()
                    this.password = password
                }
                val session = supabase.auth.currentSessionOrNull()
                
                // SECURITY: Reset failed attempts on successful login
                failedLoginAttempts = 0
                
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = session != null,
                    user = session?.user
                )
                // Don't call onSuccess here - let LaunchedEffect handle navigation
            } catch (e: Exception) {
                // SECURITY: Increment failed attempts
                failedLoginAttempts++
                
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Login failed"
                )
            }
        }
    }

    fun signUp(email: String, password: String, name: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            // SECURITY: Rate limiting for signup
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastSignUpAttempt < RATE_LIMIT_MS) {
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = "Please wait before trying again"
                )
                return@launch
            }
            
            lastSignUpAttempt = currentTime
            android.util.Log.d("AuthViewModel", "signUp started for email: $email")
            _authState.value = _authState.value.copy(isLoading = true, error = null)
            
            try {
                // Sign up with email and password, store name in user metadata
                supabase.auth.signUpWith(Email) {
                    this.email = email.trim().lowercase()
                    this.password = password
                    // Store user data in user_metadata (Supabase standard)
                    data = buildJsonObject {
                        put("full_name", name.trim())
                        put("display_name", name.trim())
                    }
                }

                android.util.Log.d("AuthViewModel", "signUp API call completed")

                // Get the session after signup
                val session = supabase.auth.currentSessionOrNull()
                android.util.Log.d("AuthViewModel", "Session after signup: ${session != null}")

                // Set authenticated state - user is logged in after signup
                // (unless email confirmation is required in Supabase settings)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    isAuthenticated = session != null,
                    user = session?.user
                )

                android.util.Log.d("AuthViewModel", "Auth state updated: isAuthenticated=${session != null}")

                // Don't call onSuccess here - let LaunchedEffect in RegisterScreen handle navigation
            } catch (e: Exception) {
                android.util.Log.e("AuthViewModel", "signUp failed: ${e.message}", e)
                _authState.value = _authState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Registration failed"
                )
            }
        }
    }

    fun signOut(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                supabase.auth.signOut()
                _authState.value = AuthState(isAuthenticated = false)
                onSuccess()
            } catch (e: Exception) {
                _authState.value = _authState.value.copy(
                    error = e.message ?: "Sign out failed"
                )
            }
        }
    }

    fun clearError() {
        _authState.value = _authState.value.copy(error = null)
    }
}
