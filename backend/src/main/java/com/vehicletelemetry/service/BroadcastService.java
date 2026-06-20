package com.vehicletelemetry.service;
import com.vehicletelemetry.dto.TelemetryDTO;
import com.vehicletelemetry.dto.WarningEventDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * BroadcastService — single point for all WebSocket outbound messages.
 * Wraps SimpMessagingTemplate with error handling and logging.
 * All services that need to push data to clients go through here.
 */
@Slf4j @Service @RequiredArgsConstructor
public class BroadcastService {
    private final SimpMessagingTemplate messaging;
    @Value("${telemetry.websocket.topic}") private String telemetryTopic;
    @Value("${telemetry.websocket.warning-topic}") private String warningTopic;

    public void broadcastTelemetry(TelemetryDTO dto) {
        try {
            messaging.convertAndSend(telemetryTopic, dto);
        } catch (Exception e) {
            log.error("Broadcast telemetry failed: {}", e.getMessage());
        }
    }

    public void broadcastWarning(WarningEventDTO dto) {
        try {
            messaging.convertAndSend(warningTopic, dto);
            log.info("⚠ {} [{}] value={}", dto.getWarningType(), dto.getSeverity(), dto.getTriggerValue());
        } catch (Exception e) {
            log.error("Broadcast warning failed: {}", e.getMessage());
        }
    }
}
