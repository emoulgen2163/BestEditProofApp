# Technical Architecture Document

This document defines the technical architecture for the **Best Edit & Proof Android App**, covering both Android client-side architecture and backend interaction.

---

## **1. Overall Architecture Overview**

The application follows a **Clean Architecture + MVVM (Model–View–ViewModel)** approach to ensure:

* Separation of concerns
* Testability
* Scalability
* Maintainability

The Android app communicates with an existing or future backend via REST APIs.

---

## **2. Android Architecture (MVVM)**

### **2.1 MVVM Explanation**

The app is structured using the MVVM pattern:

* **View (UI Layer)**

  * Built with Jetpack Compose
  * Displays state and forwards user actions
  * No business logic

* **ViewModel (Presentation Layer)**

  * Holds UI state using StateFlow / LiveData
  * Handles user actions
  * Calls domain/use-case logic

* **Model (Data Layer)**

  * Repository pattern
  * Fetches data from API or local database
  * Single source of truth

This separation ensures UI remains reactive and logic remains testable.

---

## **3. Jetpack Compose UI Plan**

* Entire UI built using **Jetpack Compose** (no XML)

* Unidirectional data flow (UDF)

* Screen-level composables:

  * LoginScreen
  * DashboardScreen
  * PlaceOrderScreen
  * OrderDetailsScreen
  * ProfileScreen

* Shared components:

  * Buttons
  * Input fields
  * Loading indicators
  * Error states

* Navigation handled via **Navigation Compose**

---

## **4. API Layer (Retrofit)**

### **4.1 Retrofit Setup**

* RESTful API communication using Retrofit
* JSON parsing via Moshi or Gson
* Coroutine-based suspend functions

Example responsibilities:

* Authentication (login)
* Fetch orders
* Submit new orders
* Upload/download documents
* Fetch user profile

### **4.2 Repository Pattern**

* Repositories abstract data sources
* Decide whether to fetch from:

  * Remote API
  * Local Room database

---

## **5. Dependency Injection (Hilt)**

* Hilt used for dependency injection
* Provides:

  * Retrofit instance
  * API services
  * Repositories
  * ViewModels

Benefits:

* Cleaner code
* Easier testing
* Lifecycle-aware injection

---

## **6. Local Database (Room)**

Room is used for **offline caching and persistence**:

* Cache:

  * Orders
  * User profile
  * Order history

* Benefits:

  * Offline access
  * Faster load times
  * Reduced API calls

Room entities mirror API DTOs where appropriate.

---

## **7. Firebase Cloud Messaging (Notifications)**

Firebase is used for push notifications:

* Order status updates
* Order completion alerts
* Important announcements

Flow:

1. Backend triggers notification event
2. Firebase sends push notification
3. App receives and updates UI

---

## **8. Backend Interaction (High-Level)**

* Backend exposes REST APIs

* Handles:

  * Authentication
  * Order processing
  * File storage
  * Payments

* Android app acts as a client only

---

## **9. Architecture Diagram (Text-Based)**

```
[ Jetpack Compose UI ]
          |
          v
[ ViewModel (StateFlow) ]
          |
          v
[ Repository ]
     |          |
     v          v
[ Retrofit ]  [ Room DB ]
     |
     v
[ Backend API ]
     |
     v
[ Firebase Notifications ]
```

---

## **10. Summary**

This architecture ensures:

* Modern Android best practices
* Scalability for future features
* Clear separation between UI, logic, and data
* Portfolio-ready professional structure

This document will serve as a reference for development, reviews, and interviews.
