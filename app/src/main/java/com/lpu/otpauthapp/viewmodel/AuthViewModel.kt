package com.lpu.otpauthapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.lpu.otpauthapp.data.OtpManager
import com.lpu.otpauthapp.data.OtpResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel(){
    private val otpManager = OtpManager()
    private val analytics: FirebaseAnalytics = Firebase.analytics

    private val _authState = MutableStateFlow<AuthState>(AuthState.EmailInput)
    val authState: StateFlow<AuthState> = _authState
    private var otpTimerJob: Job? = null
    private val otpValiditySeconds = 60

    private fun startOtpTimer(email: String) {
        otpTimerJob?.cancel()

        otpTimerJob = viewModelScope.launch {
            for (seconds in otpValiditySeconds downTo 0) {
                _authState.value = AuthState.OtpInput(
                    email = email,
                    remainingSeconds = seconds,
                    attemptsLeft = otpManager.getAttemptsLeft(email)
                )
                delay(1000)
            }
        }
    }

    fun sendOtp(email: String) {
        if (email.isBlank()) return

        otpManager.generateOtp(email)
        analytics.logEvent("otp_generated", null)

        startOtpTimer(email)
    }

    fun verifyOtp(email: String, enteredOtp: String) {
        when (val result = otpManager.validateOtp(email, enteredOtp)) {

            is OtpResult.Success -> {
                otpTimerJob?.cancel()
                analytics.logEvent("otp_validation_success", null)

                _authState.value = AuthState.LoggedIn(
                    email = email,
                    sessionStartTime = System.currentTimeMillis()
                )
            }

            is OtpResult.Error -> {
                analytics.logEvent("otp_validation_failure", null)

                val current = _authState.value
                if (current is AuthState.OtpInput) {
                    _authState.value = current.copy(
                        message = result.message,
                        attemptsLeft = otpManager.getAttemptsLeft(email)
                    )
                }
            }
        }
    }


    fun logout() {
        otpTimerJob?.cancel()
        analytics.logEvent("logout", null)
        _authState.value = AuthState.EmailInput
    }



}