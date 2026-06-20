package com.vehicletelemetry.service;
import com.vehicletelemetry.dto.TelemetryDTO;
import com.vehicletelemetry.dto.WarningEventDTO;
import com.vehicletelemetry.model.entity.WarningLog;
import com.vehicletelemetry.repository.WarningLogRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarningServiceTest {
    @Mock BroadcastService      broadcast;
    @Mock WarningLogRepository  warningLogRepo;
    @InjectMocks WarningService service;
    @Captor ArgumentCaptor<WarningEventDTO> captor;

    @BeforeEach void setUp() {
        ReflectionTestUtils.setField(service,"speedMax",200.0);
        ReflectionTestUtils.setField(service,"rpmMax",7000);
        ReflectionTestUtils.setField(service,"fuelMin",10.0);
        ReflectionTestUtils.setField(service,"tempMax",110.0);
        ReflectionTestUtils.setField(service,"battMin",11.5);
        ReflectionTestUtils.setField(service,"tireMin",28.0);
        ReflectionTestUtils.setField(service,"tireMax",38.0);
        lenient().when(warningLogRepo.save(any(WarningLog.class))).thenReturn(null);
    }

    @Test void shouldWarn_engineOverheat() {
        service.evaluateAndBroadcast(normal().engineTempCelsius(115.0).build());
        verify(broadcast, timeout(500)).broadcastWarning(captor.capture());
        assertThat(captor.getValue().getWarningType()).isEqualTo("HIGH_ENGINE_TEMP");
        assertThat(captor.getValue().getSeverity()).isEqualTo("CRITICAL");
    }

    @Test void shouldWarn_fuelCritical() {
        service.evaluateAndBroadcast(normal().fuelLevelPercent(3.0).build());
        verify(broadcast, timeout(500)).broadcastWarning(captor.capture());
        assertThat(captor.getValue().getWarningType()).isEqualTo("LOW_FUEL");
        assertThat(captor.getValue().getSeverity()).isEqualTo("CRITICAL");
    }

    @Test void shouldWarn_fuelLow() {
        service.evaluateAndBroadcast(normal().fuelLevelPercent(8.0).build());
        verify(broadcast, timeout(500)).broadcastWarning(captor.capture());
        assertThat(captor.getValue().getSeverity()).isEqualTo("WARNING");
    }

    @Test void shouldWarn_batteryLow() {
        service.evaluateAndBroadcast(normal().batteryVoltage(10.9).build());
        verify(broadcast, timeout(500)).broadcastWarning(captor.capture());
        assertThat(captor.getValue().getWarningType()).isEqualTo("LOW_BATTERY");
    }

    @Test void shouldWarn_tireLow() {
        service.evaluateAndBroadcast(normal().tirePressureFL(25.0).build());
        verify(broadcast, timeout(500)).broadcastWarning(captor.capture());
        assertThat(captor.getValue().getWarningType()).isEqualTo("LOW_TIRE_FL");
    }

    @Test void shouldNotWarn_allNormal() {
        service.evaluateAndBroadcast(normal().build());
        verify(broadcast, after(300).never()).broadcastWarning(any());
    }

    @Test void shouldRespectCooldown() {
        var t = normal().fuelLevelPercent(5.0).build();
        service.evaluateAndBroadcast(t);
        verify(broadcast, timeout(500).times(1)).broadcastWarning(any());
        service.evaluateAndBroadcast(t);
        // Still 1 — cooldown prevents re-broadcast
        verify(broadcast, after(300).times(1)).broadcastWarning(any());
    }

    private TelemetryDTO.TelemetryDTOBuilder normal() {
        return TelemetryDTO.builder().vehicleId("VH-TEST").timestamp(Instant.now())
            .speedKmh(80).rpm(2500).fuelLevelPercent(60).engineTempCelsius(90)
            .batteryVoltage(13.8)
            .tirePressureFL(32).tirePressureFR(32).tirePressureRL(32).tirePressureRR(32);
    }
}
