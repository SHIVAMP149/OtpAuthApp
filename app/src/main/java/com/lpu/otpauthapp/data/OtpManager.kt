package com.lpu.otpauthapp.data

import android.util.Log

data class OtpInfo(
    val otp: String,
    val createdAt: Long,
    var attempts: Int
)

sealed class OtpResult{
    object Success : OtpResult()
    data class Error(val message: String) : OtpResult()
}

class OtpManager{

    private val otpStore = mutableMapOf<String, OtpInfo>()
    private val otpExpireMillis = 60_000L
    private val maxAttempts = 3

    fun generateOtp(email: String) : String {
        val otp = (100000..999999).random().toString()
        otpStore[email] = OtpInfo(
            otp,
            System.currentTimeMillis(),
            0
        )

        return otp
    }



    fun validateOtp(email: String, enteredOtp: String) : OtpResult {
        val otpInfo = otpStore[email]
            ?: return OtpResult.Error("Invalid email")

//        Check Expiry
        val currentTime = System.currentTimeMillis()
        if (currentTime - otpInfo.createdAt > otpExpireMillis) {
            return OtpResult.Error("OTP expired")
        }

//        Check Attempts
        if (otpInfo.attempts >= maxAttempts) {
            otpStore.remove(email)
            return OtpResult.Error("Max attempts reached")
        }



//        Validate OTP
        return if (enteredOtp == otpInfo.otp) {
            otpStore.remove(email)
            OtpResult.Success
        } else {
            otpInfo.attempts++
            OtpResult.Error("Incorrect OTP.")
        }
    }
    fun getAttemptsLeft(email: String): Int {
        val otpInfo = otpStore[email] ?: return 0
        return maxAttempts - otpInfo.attempts
    }

}
