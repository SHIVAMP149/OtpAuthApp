package com.lpu.otpauthapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.Firebase
import com.google.firebase.analytics.analytics
import com.lpu.otpauthapp.ui.theme.LoginScreen
import com.lpu.otpauthapp.ui.theme.OtpScreen
import com.lpu.otpauthapp.ui.theme.SessionScreen
import com.lpu.otpauthapp.viewmodel.AuthState
import com.lpu.otpauthapp.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        // Initialize Firebase Analytics
        Firebase.analytics

        setContent {
            val viewModel: AuthViewModel = viewModel()
            val authState by viewModel.authState.collectAsState()

            when (authState) {

                is AuthState.EmailInput -> {
                    LoginScreen(
                        onSendOtp = { email ->
                            viewModel.sendOtp(email)
                        }
                    )
                }

                is AuthState.OtpInput -> {
                    val state = authState as AuthState.OtpInput
                    OtpScreen(
                        email = state.email,
                        message = state.message,
                        remainingSeconds = state.remainingSeconds,
                        attemptsLeft = state.attemptsLeft,
                        onVerify = { otp ->
                            viewModel.verifyOtp(state.email, otp)
                        },
                        onResend = {
                            viewModel.sendOtp(state.email)
                        }
                    )
                }

                is AuthState.LoggedIn -> {
                    val state = authState as AuthState.LoggedIn
                    SessionScreen(
                        sessionStartTime = state.sessionStartTime,
                        onLogout = {
                            viewModel.logout()
                        }
                    )
                }
            }
        }
    }
}
