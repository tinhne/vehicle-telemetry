package com.vehicletelemetry.controller;
import com.vehicletelemetry.dto.*;
import com.vehicletelemetry.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * TelemetryController — REST API endpoints.
 *
 * GET /api/telemetry/history  — paginated records (newest first)
 * GET /api/telemetry/latest   — most recent single snapshot
 * GET /api/telemetry/stats    — 24h statistics (avg speed, max speed, warning count)
 *
 * Design: vehicleId as query param (not path) because these are queries
 * on the telemetry resource, not on a specific vehicle resource.
 * Max page size capped at 200 in service layer to protect DB.
 */
@Slf4j
@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService service;

    @GetMapping("/history")
    public ResponseEntity<TelemetryHistoryResponse> history(
            @RequestParam(defaultValue = "VH-DEMO-001") String vehicleId,
            @RequestParam(defaultValue = "0")           int    page,
            @RequestParam(defaultValue = "50")          int    size) {
        log.debug("GET /history vehicleId={} page={} size={}", vehicleId, page, size);
        return ResponseEntity.ok(service.getHistory(vehicleId, page, size));
    }

    @GetMapping("/latest")
    public ResponseEntity<TelemetryDTO> latest(
            @RequestParam(defaultValue = "VH-DEMO-001") String vehicleId) {
        TelemetryDTO dto = service.getLatest(vehicleId);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/stats")
    public ResponseEntity<TelemetryStatsDTO> stats(
            @RequestParam(defaultValue = "VH-DEMO-001") String vehicleId) {
        return ResponseEntity.ok(service.getStats(vehicleId));
    }
}
