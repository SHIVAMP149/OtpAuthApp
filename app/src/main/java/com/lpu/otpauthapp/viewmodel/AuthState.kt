package com.lpu.otpauthapp.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
sealed class AuthState {

    object EmailInput : AuthState()

    data class OtpInput(
        val email: String,
        val message: String?= null,
        val remainingSeconds: Int,
        val attemptsLeft: Int
    ) : AuthState()

    data class LoggedIn(
        val email: String,
        val sessionStartTime: Long
    ) : AuthState()



}