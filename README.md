# 🚗 Vehicle Monitoring & Telemetry System

> **Production-style Android Automotive OS + Spring Boot project**
> Built for junior/fresher interviews at LG Electronics Vehicle Solution, Bosch, Harman, Hyundai Mobis

---

## 📐 Architecture Overview

```
[Spring Boot Backend]  ──WebSocket/STOMP──►  [Android Automotive App]
       │                                              │
  PostgreSQL                                    Room (local cache)
  (history)                                     Hilt + MVVM
  Scheduled                                     Compose Dashboard
  Sensor Sim                                    Foreground Service
```

**Data flow:** Sensor Simulator → BroadcastService → STOMP broker → OkHttp WebSocket → TelemetryRepository → ViewModel → Compose UI

---

## 🧰 Prerequisites

| Tool | Version | Download |
|------|---------|----------|
| IntelliJ IDEA | 2023.3+ | [jetbrains.com](https://www.jetbrains.com/idea/) |
| Android Studio | Hedgehog 2023.1.1+ | [developer.android.com](https://developer.android.com/studio) |
| Docker Desktop | 4.x | [docker.com](https://www.docker.com/products/docker-desktop/) |
| JDK 21 | 21.x | [adoptium.net](https://adoptium.net/) |
| Android SDK | API 35 | Via Android Studio SDK Manager |

---

## 🚀 Step-by-Step Setup Guide

### Step 1 — Clone and inspect the project

```bash
git clone https://github.com/yourname/vehicle-telemetry-system.git
cd vehicle-telemetry-system
```

Folder layout:
```
vehicle-telemetry-system/
├── android-app/     ← Open this in Android Studio
├── backend/         ← Open this in IntelliJ IDEA
└── docker/          ← Run docker compose from here
```

---

### Step 2 — Start the Backend with Docker

**Open a terminal in the `docker/` folder:**

```bash
cd docker
docker compose up --build
```

**What this does:**
1. Downloads `postgres:15-alpine` image
2. Creates the database `vehicle_telemetry` and runs `init.sql`
3. Builds the Spring Boot JAR inside a Docker container (takes ~2 min first time)
4. Starts both services

**Verify it's running:**
```bash
# Should see {"status":"UP"}
curl http://localhost:8080/actuator/health

# Watch live backend logs
docker compose logs -f backend
```

**Expected output in logs:**
```
[SensorSimulatorService] Tick 1 | speed=0.0 rpm=800.0 temp=20.0
[BroadcastService] Broadcast telemetry | vehicleId=VH-DEMO-001 speed=2.3
```

**Common issue — port 5432 already in use:**
```bash
# Find and kill existing postgres
lsof -i :5432
kill -9 <PID>
# OR change the port in docker-compose.yml:  "5433:5432"
```

---

### Step 3 — Open Backend in IntelliJ IDEA

1. Open **IntelliJ IDEA**
2. File → Open → select the `backend/` folder
3. IntelliJ detects it's a Gradle project → click **"Load Gradle Project"**
4. Wait for indexing to complete (~1 min)

**Set JDK 21:**
- File → Project Structure → SDK → select or download Temurin 21

**Run without Docker (for debugging):**
1. Make sure Docker Postgres is running: `docker compose up postgres -d`
2. In IntelliJ, find `VehicleTelemetryApplication.java`
3. Right-click → Run
4. The app starts on port 8080 connected to the Docker Postgres

**Set environment variables in IntelliJ Run Config:**
```
DB_URL=jdbc:postgresql://localhost:5432/vehicle_telemetry
DB_USER=telemetry_user
DB_PASS=telemetry_pass
```
- Run → Edit Configurations → Environment variables → paste above

---

### Step 4 — Set Up Android Automotive Emulator

This is the most important step. AAOS emulator is different from a regular phone emulator.

**In Android Studio:**

1. **Tools → Device Manager → + (Create Virtual Device)**
2. **Category: Automotive** (left sidebar)
3. **Select: "Automotive (1024p landscape)"** — this is the standard head unit size
4. Click **Next**
5. **System Image:** Select **"UpsideDownCake" (API 34)** for Automotive
   - If not downloaded: click the **Download** link next to it
   - Download takes 5–10 minutes
6. Click **Next → Finish**

**Start the emulator:**
- Click the ▶ play button next to the new AVD
- First boot takes 2–3 minutes — this is normal

**Verify it's an AAOS emulator:**
- You should see a car-themed launcher with large icons
- The status bar looks different from a phone (no notification dots)

**Common issue — HAXM / Virtualization error:**
```
# Windows: Enable Hyper-V in BIOS + Windows Features
# Mac M1/M2: Works natively with ARM emulator — select "ARM" system image
# Linux: sudo apt install qemu-kvm libvirt-daemon-system
```

---

### Step 5 — Open Android App in Android Studio

1. Open **Android Studio**
2. File → Open → select `android-app/` folder
3. Wait for Gradle sync (downloads ~500MB of dependencies on first run)

**If Gradle sync fails:**
```
# In android-app/gradle.properties, ensure:
org.gradle.jvmargs=-Xmx4096m

# In android-app/local.properties, ensure SDK path is set:
sdk.dir=/Users/yourname/Library/Android/sdk   # Mac
sdk.dir=C\:\\Users\\yourname\\AppData\\Local\\Android\\Sdk  # Windows
```

**Run the app:**
1. Select the **Automotive emulator** from the device dropdown (top toolbar)
2. Click ▶ Run (or Shift+F10)
3. The app installs and launches

---

### Step 6 — Connect App to Backend

The emulator communicates with your host machine (where Docker runs) via a special IP:

- **`10.0.2.2`** = host machine's localhost, from inside the Android emulator

This is already configured in `build.gradle.kts`:
```kotlin
buildConfigField("String", "WS_URL", "\"ws://10.0.2.2:8080/ws/websocket\"")
```

**You should see:**
- Dashboard screen appears with "CONNECTING..." overlay
- After 1–2 seconds: connection dot turns green, gauges start animating
- Speed, RPM, fuel, temperature — all updating every 100ms

**If connection fails:**
1. Check backend is running: `curl http://localhost:8080/actuator/health`
2. Check Docker: `docker compose ps` — both services should be "Up"
3. Check Logcat in Android Studio (filter: `VT_WebSocket`)

---

### Step 7 — Test with Postman

Import the collection: `postman/VehicleTelemetry.postman_collection.json`

**REST API endpoints:**
```
GET  http://localhost:8080/actuator/health     → Health check
GET  http://localhost:8080/api/telemetry/history  → Last 100 records
```

**WebSocket test in Postman:**
1. New Request → WebSocket
2. URL: `ws://localhost:8080/ws/websocket`
3. Connect
4. Send STOMP CONNECT frame:
```
CONNECT
accept-version:1.2
heart-beat:0,0

^@
```
5. Send SUBSCRIBE:
```
SUBSCRIBE
id:sub-0
destination:/topic/telemetry

^@
```
6. Messages should stream in every 100ms

---

## 🔧 Development Workflow

### Running Backend in IntelliJ (hot reload)

```bash
# Start only Postgres in Docker
cd docker && docker compose up postgres -d

# Run Spring Boot from IntelliJ with DevTools
# Changes to Java files → Ctrl+F9 (Build) → auto-restart
```

### Running Android App with live reload

```bash
# In Android Studio: Run → Apply Changes (Ctrl+F10)
# Faster than full reinstall for UI changes
```

### Useful ADB commands

```bash
# See all logs from the app
adb logcat -s VT_WebSocket VT_Repository VT_FgService

# Clear app data (resets Room DB and preferences)
adb shell pm clear com.vehicletelemetry

# Install APK directly to emulator
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Take screenshot from emulator
adb exec-out screencap -p > dashboard_screenshot.png
```

---

## 🧪 Running Tests

### Backend unit tests (IntelliJ)
```bash
cd backend
./gradlew test
# Report: build/reports/tests/test/index.html
```

### Android unit tests (Android Studio)
```bash
cd android-app
./gradlew test
# OR: right-click test file → Run
```

### Manual WebSocket reconnect test
1. Start app — wait for green connection indicator
2. `docker compose stop backend` — simulate server crash
3. Watch Logcat: `Reconnect attempt 1 in 2000ms`, `...2 in 4000ms`, etc.
4. `docker compose start backend` — connection should auto-recover

---

## 📁 Key Files Reference

| File | Purpose |
|------|---------|
| `SensorSimulatorService.java` | Generates realistic vehicle data every 100ms |
| `WarningService.java` | Threshold evaluation + debounce |
| `WebSocketConfig.java` | STOMP broker configuration |
| `TelemetryWebSocketClient.kt` | Android WebSocket + STOMP + reconnect |
| `TelemetryRepositoryImpl.kt` | DTO → Domain model mapping |
| `DashboardViewModel.kt` | UI state management |
| `DashboardScreen.kt` | Main dashboard Compose UI |
| `SpeedGauge.kt` | Canvas-drawn speedometer |
| `TelemetryForegroundService.kt` | Keeps connection alive in background |
| `docker-compose.yml` | Full stack orchestration |

---

## 🗓 7-Day Development Roadmap

| Day | Focus | Deliverable |
|-----|-------|-------------|
| 1 | Architecture + Project setup | Both projects compile, emulator boots |
| 2 | Backend WebSocket + Simulator | Backend broadcasts data, testable in Postman |
| 3 | Android WebSocket client | App connects, raw JSON visible in Logcat |
| 4 | Dashboard UI + Gauges | Animated speedometer and RPM gauge working |
| 5 | Warning system + Foreground Service | Alerts appear on dashboard |
| 6 | History screen + REST API + Tests | Full feature complete |
| 7 | Polish, README, Git cleanup | Interview-ready demo |

### Git commit strategy (Conventional Commits)
```bash
git commit -m "feat(backend): add STOMP WebSocket configuration"
git commit -m "feat(android): implement TelemetryWebSocketClient with reconnect"
git commit -m "feat(ui): add SpeedGauge Canvas component"
git commit -m "fix(websocket): handle STOMP heartbeat frame correctly"
git commit -m "test(backend): add WarningService threshold unit tests"
git commit -m "docs: update README with emulator setup guide"
```

---

## 🎤 Interview Talking Points

**"Why Foreground Service?"**
> "In Android Automotive, the system can kill background processes under memory pressure. A Foreground Service with a persistent notification signals to Android that this is critical user-facing work. It also receives START_STICKY so Android restarts it after OOM kills."

**"Why STOMP over raw WebSocket?"**
> "STOMP adds topic-based routing over raw WebSocket. Our Android client subscribes to /topic/telemetry and /topic/warnings independently. This makes it easy to add new data streams without changing the connection logic — you just add a new subscription."

**"Why exponential backoff for reconnect?"**
> "If the backend restarts and 1000 vehicles all try to reconnect simultaneously at fixed intervals, you get a thundering herd problem — the server gets overwhelmed right when it's trying to recover. Exponential backoff with jitter spreads out reconnection attempts, giving the server time to recover."

**"Why sealed class for WebSocketState?"**
> "Sealed classes give us exhaustive when expressions — the compiler forces you to handle every state. This prevents bugs where the UI shows a stale 'Connecting' spinner because a developer forgot to handle the Error state."

**"What is CAN Bus?"**
> "CAN (Controller Area Network) Bus is the standard vehicle network protocol (ISO 11898). Every ECU — engine, transmission, ABS — broadcasts frames at speeds up to 1Mbps. In production, our backend's SensorSimulatorService would be replaced by a CAN Bus adapter reading real PID data."
# vehicle-telemetry
