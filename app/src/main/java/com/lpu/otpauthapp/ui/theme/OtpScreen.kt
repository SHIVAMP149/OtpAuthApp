package com.lpu.otpauthapp.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun OtpScreen(
    email: String,
    message: String?,
    remainingSeconds: Int,
    attemptsLeft: Int,
    onVerify: (String) -> Unit,
    onResend: () -> Unit
) {
    var otp by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Verify OTP",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Enter the 6-digit code sent to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Text(
                    text = email,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OTP Input
                OutlinedTextField(
                    value = otp,
                    onValueChange = {
                        if (it.length <= 6 && it.all { ch -> ch.isDigit() }) {
                            otp = it
                        }
                    },
                    label = { Text("OTP") },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Countdown text
                Text(
                    text = if (remainingSeconds > 0)
                        "OTP expires in ${remainingSeconds}s"
                    else
                        "OTP expired",
                    color = if (remainingSeconds > 0)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = remainingSeconds / 60f,
                    modifier = Modifier.fillMaxWidth()
                )
                // 🔁 Attempts left
                Text(
                    text = "Attempts left: $attemptsLeft",
                    color = if (attemptsLeft > 1)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )



                // Progress bar


                // Error message
                message?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Verify button
                Button(
                    onClick = { onVerify(otp) },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = otp.length == 6 && remainingSeconds > 0
                ) {
                    Text("Verify OTP")
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Resend button (disabled until expiry)
                TextButton(
                    onClick = onResend,
                    enabled = remainingSeconds == 0
                ) {
                    Text("Resend OTP")
                }
            }
        }
    }
}
