# CoinOverlay

CoinOverlay is a production-ready native Android application built with Kotlin, Jetpack Compose, Material 3, MVVM, Repository Pattern, DataStore, Foreground Service, and WindowManager overlay support.

## Features

- Transparent overlay using `TYPE_APPLICATION_OVERLAY`
- Foreground service for persistent background execution
- Boot auto-start and package update auto-restart
- Binance realtime WebSocket ticker stream
- Auto reconnect with backoff
- Always-on-top floating overlay
- Position save and restore
- Drag overlay to move
- Manual X and Y adjustment
- Touch-through mode
- Lock and unlock overlay position
- Font size adjustment
- Font color adjustment
- Shadow toggle
- Opacity control
- Bold text mode
- Dark mode and light mode
- Add coin
- Remove coin
- Search coin
- Favorite coin
- Realtime price updates
- Low memory and low battery oriented update flow
- Compose UI with single-activity architecture

## Requirements

- Android Studio Ladybug or newer
- JDK 17
- Android SDK Platform 35
- Android Build Tools installed through Android Studio
- Internet connection for Binance API/WebSocket access

## Project Setup

1. Extract the project ZIP.
2. Open Android Studio.
3. Choose **Open** and select the `CoinOverlay` project folder.
4. Allow Gradle sync to complete.
5. Connect an Android 10+ device or start an emulator.
6. Build and run the app.

## How to Build

### Debug build

```bash
./gradlew assembleDebug
```

### Release build

```bash
./gradlew assembleRelease
```

## How to Generate APK

### Debug APK

```bash
./gradlew assembleDebug
```

Output path:

```text
app/build/outputs/apk/debug/app-debug.apk
```

### Release APK

```bash
./gradlew assembleRelease
```

Output path:

```text
app/build/outputs/apk/release/app-release.apk
```

## How to Generate AAB

```bash
./gradlew bundleRelease
```

Output path:

```text
app/build/outputs/bundle/release/app-release.aab
```

## Common Commands

```bash
./gradlew clean
./gradlew assembleDebug
./gradlew assembleRelease
./gradlew bundleRelease
./gradlew lint
./gradlew test
```

## Overlay Permission

The app requires the **Display over other apps** permission to show the overlay. On first launch:

1. Open the app.
2. Tap **Grant Overlay Permission**.
3. Enable permission for CoinOverlay.
4. Return to the app and start the overlay.

## Foreground Service Notes

Android requires a persistent notification while the overlay service is active. CoinOverlay creates a low-priority notification channel and keeps the overlay alive through a foreground service.

## Boot Auto Start

If enabled in Settings, the overlay service starts again after:

- Device reboot
- App update / package replaced
- Some vendor quick boot broadcasts

## Binance Data Source

The app uses:

- Binance Exchange Info REST API for searchable tradable USDT pairs
- Binance combined stream WebSocket ticker endpoint for realtime price updates

## Cloud Build Compatibility

This project is structured to work with:

- Android Studio
- Gradle Wrapper
- GitHub Actions
- Codemagic
- Appcircle
- Other Android cloud build services supporting Gradle Kotlin DSL and JDK 17

## Signing Release Builds

For production release signing, configure signing in `app/build.gradle.kts` or through CI environment variables and a secure keystore file.

## Package Name

```text
com.coinoverlay
```

## Min / Target SDK

- Min SDK: 29
- Target SDK: 35

## Architecture

- UI: Jetpack Compose + Material 3
- Architecture: MVVM
- Data: Repository Pattern
- Persistence: DataStore Preferences
- Networking: OkHttp WebSocket + REST
- Background execution: Foreground Service
- Overlay system: WindowManager

## Notes

- The launcher icon is vector-based and adaptive.
- The app only updates ticker UI state when new stream data arrives.
- The overlay remains lightweight by using a single service, a single WebSocket connection, and state-driven Compose rendering.