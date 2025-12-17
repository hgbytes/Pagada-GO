package com.pagadasports.pagada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pagadasports.pagada.data.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PasswordResetStep {
    object EnterEmail : PasswordResetStep()
    data class VerifyOTP(val email: String) : PasswordResetStep()
    data class ResetPassword(val email: String) : PasswordResetStep()
    object Success : PasswordResetStep()
}

data class PasswordResetState(
    val currentStep: PasswordResetStep = PasswordResetStep.EnterEmail,
    val isLoading: Boolean = false,
    val error: String? = null,
    val email: String = "",
    val successMessage: String = ""
)

class PasswordResetViewModel : ViewModel() {
    private val supabase = SupabaseClient.client

    private val _resetState = MutableStateFlow(PasswordResetState())
    val resetState: StateFlow<PasswordResetState> = _resetState.asStateFlow()

    fun sendResetEmail(email: String) {
        viewModelScope.launch {
            _resetState.value = _resetState.value.copy(
                isLoading = true,
                error = null,
                email = email
            )
            
            try {
                // SECURITY: Supabase handles password reset securely
                supabase.auth.resetPasswordForEmail(email)
                
                _resetState.value = _resetState.value.copy(
                    isLoading = false,
                    currentStep = PasswordResetStep.Success,
                    successMessage = "Password reset link sent to your email!"
                )
            } catch (e: Exception) {
                _resetState.value = _resetState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to send reset email. Please try again."
                )
            }
        }
    }

    fun clearError() {
        _resetState.value = _resetState.value.copy(error = null)
    }

    fun resetFlow() {
        _resetState.value = PasswordResetState()
    }
}
