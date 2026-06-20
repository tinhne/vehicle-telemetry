package com.vehicletelemetry.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

/**
 * WebSocketConfig — configures STOMP over WebSocket with SockJS fallback.
 *
 * Why STOMP instead of raw WebSocket?
 * STOMP adds topic-based routing (/topic/telemetry, /topic/warnings).
 * Add new data streams without changing client connection code — just add a SUBSCRIBE.
 *
 * Why SockJS fallback?
 * Some corporate/vehicle networks block WebSocket upgrades on ports 80/443.
 * SockJS transparently falls back to long-polling, keeping connectivity.
 *
 * In production at scale: replace enableSimpleBroker with a RabbitMQ relay
 * for horizontal scaling across multiple backend pods.
 */
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // In-memory broker for pub/sub
        registry.enableSimpleBroker("/topic");
        // All @MessageMapping methods prefixed with /app
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureWebSocketTransport(WebSocketTransportRegistration reg) {
        reg.setMessageSizeLimit(64 * 1024)   // 64KB per message
           .setSendTimeLimit(10_000)                  // 10s send timeout
           .setSendBufferSizeLimit(512 * 1024);        // 512KB buffer per session
    }
}
