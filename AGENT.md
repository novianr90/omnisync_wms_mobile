# Omnisync WMS Mobile - Agent Workspace & Architecture Guide

Welcome to the Omnisync WMS Mobile developer and agent context registry. This file documents the structural details, UI/UX guidelines, network architecture, and development best practices for the mobile client.

---

## 🗺️ Project Architecture Overview

The WMS Mobile app is built using **Compose Multiplatform (Kotlin)**. This allows the application to be written once in Kotlin and deployed natively to both Android and iOS devices.

### 1. The Shared UI Module (`shared/`)
- **Location**: `shared/src/commonMain/kotlin`
- **Responsibility**: Houses 100% of the UI, Navigation, Networking, and State Management.
- **Key Files**:
  - `App.kt`: The main entry point containing the navigation graph.
  - `theme/Theme.kt` & `theme/Color.kt`: The Compose Material implementation of the high-contrast industrial design system.
  - `ui/`: Contains the screen definitions (`LoginScreen`, `DashboardScreen`, `ScanScreen`).
  - `network/ApiClient.kt`: The Ktor HTTP client and data models.

### 2. Platform Targets
- **`androidApp/`**: The standard Android application wrapper.
- **`iosApp/`**: The Xcode project that compiles the shared Kotlin framework into a native iOS app.

---

## 🎨 UI/UX & Design System (`DESIGN.md`)

The application is designed specifically for **Industrial Warehouse Environments**.
- **Contrast & Legibility**: Strict adherence to the `DESIGN.md` color palette (Deep slate `#0f172a`, Error `#ba1a1a`, Surface `#f8f9ff`).
- **Typography**: Uses the `Work Sans` font family for high legibility on variable lighting conditions.
- **Touch Targets**: All interactive elements (buttons, inputs) must be large and accessible (minimum 48dp/64dp heights) to accommodate gloved operation.

---

## 🌐 Networking Layer

The mobile app connects to the `omnisync_wms` Dashboard Service REST API.
- **Client**: Ktor Client (`io.ktor:ktor-client-core`).
- **Serialization**: `kotlinx.serialization` (JSON parsing).
- **Authentication**: JWT Bearer Tokens. The `ApiClient` automatically attaches the token after a successful login.
- **Base URL Configuration**: 
  - When running locally on an Android Emulator, the backend is accessed via `http://10.0.2.2:9901/api/v1/` because `10.0.2.2` bridges to the host machine's `localhost`.

---

## ⚠️ AI Agent Development Gotchas & Best Practices

1. **Compose Multiplatform Limitations**:
   - Only use pure Kotlin libraries in the `commonMain` source set. Android-specific libraries (like Retrofit, Room, or Android View APIs) cannot be used in `commonMain`. 
   - Always use Ktor for networking and Multiplatform Settings for key-value storage.
2. **State Hoisting**:
   - Keep screens as stateless as possible by passing down parameters and callbacks. Use `rememberCoroutineScope()` carefully within Composables.
3. **Compilation**:
   - To build the Android app via CLI, use `./gradlew :composeApp:assembleDebug` (or `androidApp` depending on module naming in future).
   - If the environment lacks the Android SDK locally, rely on cloud IDEs like **Google Project IDX** which provide pre-configured Android emulators in the browser.

---

## 💡 Tech Stack Checklist
- **Language**: Kotlin 1.9.21
- **UI Toolkit**: Jetpack Compose / Compose Multiplatform
- **Networking**: Ktor Client v2.3.7
- **Serialization**: Kotlinx Serialization v1.6.2
- **Build System**: Gradle Kotlin DSL
