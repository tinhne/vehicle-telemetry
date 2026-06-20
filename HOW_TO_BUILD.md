# 📋 Hướng Dẫn Chi Tiết — Cách Làm, Thứ Tự, Lối Tư Duy

---

## PHẦN 1: Lối tư duy khi bắt đầu một project như thế này

### Bước đầu tiên: Vẽ luồng dữ liệu TRƯỚC KHI code

```
Câu hỏi cần trả lời trước khi viết 1 dòng code:
1. Data đến từ đâu?     → Backend sensor simulator
2. Data đi đâu?         → Android dashboard UI
3. Giao tiếp bằng gì?   → WebSocket (real-time push)
4. Format dữ liệu?      → JSON qua STOMP
5. Khi mất kết nối?     → Exponential backoff reconnect
6. Khi background?      → Foreground Service giữ kết nối
```

### Nguyên tắc: Backend trước, Android sau

Lý do: Android là consumer của data, Backend là producer.
Không thể test Android nếu Backend chưa chạy.

### Nguyên tắc: Làm từ dưới lên (bottom-up)

```
Tầng infrastructure (DB, Network) → Tầng data → Tầng domain → Tầng UI

ĐÚNG:  Room Entity → DAO → DB → Repository → UseCase → ViewModel → Screen
SAI:   Screen → ViewModel → UseCase → ... (bị unresolved import ngay lập tức)
```

---

## PHẦN 2: Thứ tự làm việc thực tế (7 ngày)

### Ngày 1 — Setup môi trường + Docker

```bash
# Tạo thư mục project
mkdir vehicle-telemetry-system
cd vehicle-telemetry-system
git init
```

**Việc cần làm:**
1. Tạo `docker/docker-compose.yml` và `docker/postgres/init.sql`
2. Chạy `docker compose up postgres -d`
3. Verify: `docker exec -it vt_postgres psql -U telemetry_user -d vehicle_telemetry -c "\dt"`
4. Commit: `git commit -m "feat(docker): add PostgreSQL setup"`

**Tại sao Docker trước?**
Backend cần DB để start. Nếu làm Backend trước mà không có DB, Spring Boot sẽ crash khi start.

---

### Ngày 2 — Backend Foundation

**Thứ tự tạo files:**

```
1. build.gradle           — dependencies trước
2. settings.gradle
3. application.yml        — cấu hình DB, WebSocket, thresholds
4. VehicleTelemetryApplication.java  — entry point
5. config/WebSocketConfig.java       — STOMP broker
6. config/SecurityConfig.java        — CORS
```

**Tại sao cấu hình trước code?**
Spring Boot đọc application.yml lúc start. Nếu thiếu cấu hình, @Value injection sẽ fail → NullPointerException.

**Test:**
```bash
cd backend
./gradlew bootRun
# Terminal khác:
curl http://localhost:8080/actuator/health
# {"status":"UP"}  ← backend chạy
```

---

### Ngày 3 — Backend Data Layer + Services

**Thứ tự:**
```
1. dto/TelemetryDTO.java             — DTO trước (không import gì)
2. dto/WarningEventDTO.java
3. dto/TelemetryHistoryResponse.java
4. dto/TelemetryStatsDTO.java
5. model/entity/TelemetryRecord.java  — Entity (import DTO)
6. model/entity/WarningLog.java
7. repository/TelemetryRecordRepository.java  — Repository (import Entity)
8. repository/WarningLogRepository.java
9. service/BroadcastService.java     — Service đơn giản trước
10. service/TelemetryService.java
11. service/WarningService.java      — Service phức tạp sau
12. service/SensorSimulatorService.java  — Cuối cùng vì dùng tất cả services trên
13. controller/TelemetryController.java
14. exception/GlobalExceptionHandler.java
15. config/SchedulerConfig.java
```

**Tại sao thứ tự này?**
```
BroadcastService không import gì → an toàn làm trước
WarningService cần BroadcastService → làm sau
SensorSimulatorService cần BroadcastService + WarningService + TelemetryService
→ làm CUỐI cùng
```

**Test sau ngày 3:**
```bash
# Xem log — phải thấy data streaming
docker compose logs -f backend | grep "tick="

# Test REST API
curl "http://localhost:8080/api/telemetry/latest?vehicleId=VH-DEMO-001"

# Test WebSocket bằng Postman:
# ws://localhost:8080/ws/websocket → CONNECT → SUBSCRIBE /topic/telemetry
```

---

### Ngày 4 — Android Foundation

**Mở Android Studio:**
```
File → Open → chọn thư mục android/
```

**Thứ tự tạo files:**

```
TẦNG 1 — Không phụ thuộc gì:
  gradle/libs.versions.toml     ← version catalog, thêm trước nhất
  build.gradle.kts (ROOT)       ← apply false
  settings.gradle.kts
  gradle.properties             ← android.useAndroidX=true
  app/build.gradle.kts          ← dependencies

TẦNG 2 — Core (không import Android):
  core/constants/AppConstants.kt

TẦNG 3 — Data Remote:
  data/remote/dto/Dtos.kt           ← mirror JSON
  data/remote/websocket/WebSocketState.kt
  data/remote/websocket/TelemetryWebSocketClient.kt

TẦNG 4 — Data Local:
  data/local/db/entity/TelemetryEntity.kt
  data/local/db/dao/TelemetryDao.kt
  data/local/db/TelemetryDatabase.kt

TẦNG 5 — Repository:
  data/repository/TelemetryRepository.kt       ← interface
  data/repository/TelemetryRepositoryImpl.kt   ← implementation

TẦNG 6 — Domain (pure Kotlin):
  domain/model/TelemetryData.kt
  domain/usecase/UseCases.kt

TẦNG 7 — DI (Hilt):
  core/di/NetworkModule.kt
  core/di/DatabaseModule.kt
  core/di/RepositoryModule.kt
```

**Gradle Sync sau mỗi tầng:**
Build → Make Project (Cmd+F9). Nếu có lỗi đỏ → fix ngay, không để tích lũy.

---

### Ngày 5 — Android UI

```
TẦNG 8 — Utilities:
  core/extensions/FlowExtensions.kt
  core/utils/NetworkUtils.kt

TẦNG 9 — UI Foundation:
  presentation/theme/Theme.kt          ← colors trước
  presentation/navigation/AppNavigation.kt

TẦNG 10 — Components (không cần ViewModel):
  presentation/dashboard/components/Gauges.kt
  presentation/dashboard/components/StatusPanels.kt

TẦNG 11 — ViewModels (cần UseCases):
  presentation/dashboard/DashboardViewModel.kt
  presentation/history/HistoryViewModel.kt
  presentation/settings/SettingsViewModel.kt

TẦNG 12 — Screens (cần ViewModels + Components):
  presentation/dashboard/DashboardScreen.kt
  presentation/history/HistoryScreen.kt
  presentation/settings/SettingsScreen.kt

TẦNG 13 — Entry Points (cuối cùng):
  presentation/main/MainActivity.kt
  service/TelemetryForegroundService.kt
  VehicleTelemetryApp.kt               ← LUÔN cuối cùng
```

---

### Ngày 6 — Resources + Emulator

**Resources:**
```
app/src/main/AndroidManifest.xml
app/src/main/res/values/themes.xml
app/src/main/res/values/strings.xml
app/src/main/res/drawable/ic_car.xml
app/src/main/res/xml/network_security_config.xml
```

**Tạo AAOS Emulator:**
```
Tools → Device Manager → +
→ Automotive (1024p landscape)
→ UpsideDownCake API 34 (download nếu chưa có)
→ Boot emulator
```

**Run app:**
```
Device dropdown → AAOS_API34 → Run
```

**Debug kết nối:**
```
Logcat → filter: VT_WebSocket
Phải thấy:
  I/VT_WebSocket: Connecting → ws://10.0.2.2:8080/ws/websocket
  I/VT_WebSocket: ✓ Connection opened
  I/VT_WebSocket: STOMP CONNECT + SUBSCRIBE sent
```

---

### Ngày 7 — Tests + Polish

**Unit Tests:**
```bash
# Backend
cd backend && ./gradlew test

# Android (trong Android Studio)
Right-click DashboardViewModelTest → Run
```

**Git commit convention:**
```bash
git commit -m "feat(backend): add STOMP WebSocket configuration"
git commit -m "feat(android): implement TelemetryWebSocketClient with reconnect"
git commit -m "feat(ui): add SpeedGauge Canvas component with spring animation"
git commit -m "fix(websocket): handle STOMP heartbeat frame correctly"
git commit -m "test(backend): add WarningService threshold unit tests"
git commit -m "test(android): add DashboardViewModel unit tests with Turbine"
git commit -m "docs: add comprehensive README and setup guide"
```

---

## PHẦN 3: Phân tích kiến trúc — Tại sao mỗi quyết định?

### Tại sao Clean Architecture (3 tầng)?

```
Tầng Data: TelemetryWebSocketClient, TelemetryRepositoryImpl, Room
Tầng Domain: TelemetryData, UseCases (pure Kotlin, zero Android)
Tầng Presentation: ViewModel, Compose screens

Lợi ích:
- Test Domain layer mà không cần Android emulator
- Swap WebSocket → MQTT: chỉ sửa Data layer
- Backend thay đổi JSON: chỉ sửa DTO + mapper trong Data layer
- ViewModel không bao giờ import OkHttp, Room, hoặc Android Context
```

### Tại sao MVVM + StateFlow?

```kotlin
// ViewModel expose 1 StateFlow duy nhất
val uiState: StateFlow<DashboardUiState>

// Compose collect với lifecycle awareness
val state by vm.uiState.collectAsStateWithLifecycle()

// Lợi ích:
// 1. Survive rotation: ViewModel outlives Activity
// 2. replay=1 trên telemetryFlow: screen ngay lập tức có data sau rotation
// 3. Immutable UI state: không mutation, debug dễ hơn
// 4. collectAsStateWithLifecycle: tự pause khi app background → tiết kiệm battery
```

### Tại sao Hilt cho DI?

```kotlin
// Không có Hilt:
class MainActivity : AppCompatActivity() {
    private val gson = Gson()
    private val okHttpClient = OkHttpClient.Builder()...build()
    private val wsClient = TelemetryWebSocketClient(okHttpClient, gson)
    private val repo = TelemetryRepositoryImpl(wsClient)
    // ... manual wiring, không testable, không reusable
}

// Với Hilt:
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    // Hilt inject tất cả ↑ tự động
    // Test: swap RepositoryModule → TestRepositoryModule
}
```

### Tại sao Repository Pattern?

```
ViewModel → UseCase → Repository (interface) → RepositoryImpl → OkHttp/Room

Nếu không có Repository:
ViewModel trực tiếp gọi OkHttp → không test được, không swap được

Test ViewModel:
class FakeTelemetryRepository : TelemetryRepository {
    override fun getLiveTelemetry() = flowOf(TelemetryData.EMPTY)
}
// Inject FakeRepository vào ViewModel → test hoàn toàn isolated
```

### Tại sao Foreground Service?

```
Scenario thực tế:
1. Driver xem dashboard (WS connected ✓)
2. Driver mở Google Maps (app bị background)
3. Android giết process sau 5 phút (memory pressure)
4. Driver quay lại dashboard → màn hình trắng

Với Foreground Service:
- Android thấy notification "Vehicle Telemetry - Monitoring..."
- Android KHÔNG giết process này
- Dashboard luôn có data khi driver quay lại

Automotive-specific:
// foregroundServiceType="connectedDevice" (required Android 14+)
// Mô tả đúng: "service này duy trì network connection tới vehicle"
```

### Tại sao Exponential Backoff?

```
Scenario: Server crash, 1000 xe mất kết nối cùng lúc.

Fixed retry mỗi 2s:
  t=2s: 1000 xe kết nối lại → server quá tải → crash lại
  t=4s: 1000 xe kết nối lại → server quá tải → crash lại
  ... vòng lặp vô tận

Exponential backoff với jitter:
  Xe 1: retry sau 2.1s
  Xe 2: retry sau 2.4s
  Xe 3: retry sau 1.9s
  ... phân tán ngẫu nhiên → server phục hồi từ từ
```

---

## PHẦN 4: Cách đọc code người khác viết (phân tích)

Khi nhận code review hoặc interview, đọc theo thứ tự:

1. **README** — hiểu purpose
2. **Architecture diagram** — hiểu luồng tổng thể
3. **build.gradle / libs.versions.toml** — hiểu dependencies
4. **Domain models** (TelemetryData.kt) — hiểu business entities
5. **Repository interface** (TelemetryRepository.kt) — hiểu contract
6. **ViewModel** (DashboardViewModel.kt) — hiểu UI state
7. **Entry point** (MainActivity.kt, Application.kt) — hiểu lifecycle

Câu hỏi cần đặt khi đọc code:
- "File này phụ thuộc vào gì?" → dependencies
- "File này được dùng ở đâu?" → dependents
- "Nếu tôi thay đổi file này, điều gì bị ảnh hưởng?"

---

## PHẦN 5: Common Bugs và cách debug

| Bug | Triệu chứng | Debug | Fix |
|-----|-------------|-------|-----|
| WS không kết nối | `VT_WebSocket: Failure: Failed to connect` | `curl localhost:8080/actuator/health` | Đảm bảo backend đang chạy |
| Cleartext blocked | `CLEARTEXT communication not permitted` | Logcat filter `cleartext` | Thêm network_security_config.xml |
| Hilt crash | `Cannot create instance of ViewModel` | Logcat full error | Kiểm tra @HiltAndroidApp, @AndroidEntryPoint |
| Room crash | `Cannot access database on the main thread` | Stack trace | Dùng `suspend` hoặc `Flow` trong DAO |
| Gradle sync fail | `Plugin not found` | Build Output tab | Kiểm tra libs.versions.toml versions |
| Emulator no network | Ping 10.0.2.2 fails | `adb shell ping 10.0.2.2` | Check Windows Firewall / VPN |
