# OTP Authentication App (Passwordless Login)

## Overview

This Android application implements a **passwordless authentication flow** using **Email + OTP**, followed by a **session screen** that tracks the login duration in real time.

The app is built **without any backend**. All OTP generation, validation, expiry handling, and session tracking are done **locally**, as required by the assignment.

The project focuses on **correct architecture, state management, and Jetpack Compose usage**, rather than UI complexity.

---

## Tech Stack

* **Language:** Kotlin
* **UI:** Jetpack Compose (Material 3)
* **Architecture:** ViewModel + UI State (One-way data flow)
* **Concurrency:** Kotlin Coroutines
* **Analytics SDK:** Firebase Analytics
* **IDE:** Android Studio

---

## Authentication Flow

1. User enters an **email address**
2. App generates a **6-digit OTP locally**
3. User enters the OTP
4. OTP is validated based on rules
5. On success, user is navigated to the **Session Screen**
6. Session duration is displayed live
7. User can logout to end the session

---

## OTP Rules & Handling

### Implemented Rules

* OTP length: **6 digits**
* OTP expiry: **60 seconds**
* Maximum attempts: **3**
* Resending OTP:

  * Invalidates the previous OTP
  * Resets attempt count
  * Restarts countdown timer

### How Expiry Works

* Each OTP stores a `createdAt` timestamp
* Expiry is checked by comparing current system time with creation time
* OTP is invalidated automatically after 60 seconds

---

## 🧩 Data Structures Used

### `Map<String, OtpInfo>`

* **Key:** Email
* **Value:** `OtpInfo` data class

```kotlin
data class OtpInfo(
    val otp: String,
    val createdAt: Long,
    var attempts: Int
)
```

### Why this structure?

* Fast lookup by email
* Clean separation of OTP per user
* No global mutable state
* Easy invalidation and reset

---

## Architecture & State Management

### ViewModel

* `AuthViewModel` handles:

  * OTP generation & validation
  * OTP countdown timer
  * Attempts tracking
  * Session start time
* Uses `StateFlow` to expose UI state

### UI State (Sealed Class)

```kotlin
sealed class AuthState {
    object EmailInput
    data class OtpInput(
        val email: String,
        val message: String?,
        val remainingSeconds: Int,
        val attemptsLeft: Int
    )
    data class LoggedIn(
        val email: String,
        val sessionStartTime: Long
    )
}
```

### Benefits

* Clear screen transitions
* One-way data flow
* State survives recomposition and screen rotation
* No UI logic inside ViewModel

---

## OTP Countdown Timer & Attempts Tracking

* Countdown timer is managed inside the **ViewModel** using coroutines
* Remaining seconds are exposed to UI via state
* UI displays:

  * Remaining time
  * Progress bar
  * Attempts left
* **Resend OTP button is disabled until expiry**
* **Verify button is disabled when OTP expires or attempts reach zero**

---

## Session Screen

* Session start time is stored in ViewModel
* Live session duration is calculated using:

  ```
  currentTime - sessionStartTime
  ```
* Timer:

  * Updates every second
  * Survives recomposition
  * Stops automatically on logout

---

## Firebase Analytics Integration

### Why Firebase Analytics?

* Lightweight
* Industry-standard
* No backend required
* Easy event tracking

### Events Logged

* `otp_generated`
* `otp_validation_success`
* `otp_validation_failure`
* `logout`

Firebase Analytics is initialized in `MainActivity`, and events are logged from the ViewModel.

---

## Testing Strategy

### Manual Testing

* OTP generation & validation
* Incorrect OTP attempts
* Max attempt limit (3 tries)
* OTP expiry after 60 seconds
* Resend OTP behavior
* Session timer accuracy
* Screen rotation handling

### Debugging OTP

For testing purposes, OTP values were temporarily logged to **Logcat**.
(This was used only during development and is not required in production.)

---

### GPT Usage Disclosure

GPT was used for:

* Understanding assignment requirements
* Clarifying Jetpack Compose and ViewModel concepts
* Debugging compile-time and dependency errors
* Structuring the solution step by step

GPT was not used to copy-paste a full project.
All logic was implemented with personal understanding and iterative development.

---

### How to Run the App

1. Clone the repository
2. Open the project in Android Studio
3. Connect firebase to your clone repository
4. Add `google-services.json` to the `/app` directory
5. Sync Gradle
6. Run on an emulator or physical device


## Conclusion

This project demonstrates:

* Proper use of Jetpack Compose
* Clean ViewModel-based architecture
* Correct state management
* Time-based logic handling
* External SDK integration
* Defensive and readable code
