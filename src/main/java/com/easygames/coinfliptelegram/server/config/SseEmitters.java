package com.easygames.coinfliptelegram.server.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Component
@Slf4j
public class SseEmitters {
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(this::sendHeartbeat, 0, 30, TimeUnit.SECONDS);
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdown();
    }

    private void sendHeartbeat() {
        send(SseEmitter.event()
                .id("heartbeat")
                .name("heartbeat")
                .data("ping")
                .build());
    }

    public void add(String userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        log.info("Added SSE emitter for user: {}", userId);
    }

    public void remove(String userId) {
        SseEmitter emitter = emitters.remove(userId);
        if (emitter != null) {
            try {
                emitter.complete();
                log.info("Removed and completed SSE emitter for user: {}", userId);
            } catch (Exception e) {
                log.error("Error completing SSE emitter for user: {}", userId, e);
            }
        } else {
            log.warn("Attempted to remove non-existent SSE emitter for user: {}", userId);
        }
    }

    public void send(Object data) {
        List<String> deadEmitterIds = new ArrayList<>();
        emitters.forEach((userId, emitter) -> {
            try {
                emitter.send(data);
            } catch (IOException e) {
                log.error("Error sending SSE data to user {}: ", userId, e);
                deadEmitterIds.add(userId);
            } catch (Exception e) {
                log.error("Unexpected error while sending SSE data to user {}: ", userId, e);
                deadEmitterIds.add(userId);
            }
        });

        deadEmitterIds.forEach(this::remove);

        if (emitters.isEmpty()) {
            log.warn("All SSE emitters have been removed. No active connections.");
        } else {
            log.info("Remaining active SSE emitters: {}", emitters.size());
        }
    }

    public boolean hasActiveEmitters() {
        return !emitters.isEmpty();
    }

    public int getActiveEmittersCount() {
        return emitters.size();
    }
}
