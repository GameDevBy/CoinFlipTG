package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.config.SseEmitters;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.model.Game;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sse/games")
@AllArgsConstructor
public class GameSSEController {

    private final SseEmitters sseEmitters;
    private final ModelMapper modelMapper;

    @GetMapping(value = "/{userId}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGames(@PathVariable String userId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.add(userId, emitter);

        emitter.onCompletion(() -> sseEmitters.remove(userId));
        emitter.onTimeout(() -> sseEmitters.remove(userId));
        emitter.onError((e) -> {
            log.error("SSE error for user {}", userId, e);
            sseEmitters.remove(userId);
        });

        // Send an initial event to establish the connection
        try {
            emitter.send(SseEmitter.event().id("connect").name("connect").data("Connected").build());
        } catch (IOException e) {
            log.error("Error sending initial SSE event to user {}", userId, e);
            sseEmitters.remove(userId);
        }

        log.info("New SSE connection established for user {}. Total active connections: {}",
                userId, sseEmitters.getActiveEmittersCount());
        return emitter;
    }

    @PostMapping("/close/{userId}")
    public ResponseEntity<Void> closeSSEConnection(@PathVariable String userId) {
        sseEmitters.remove(userId);
        return ResponseEntity.ok().build();
    }

    // Call this method when a game is created, updated, or deleted
    public void sendGameUpdate(String type, Game game) {
        if (!sseEmitters.hasActiveEmitters()) {
            log.warn("Attempted to send game update, but no active SSE connections.");
            return;
        }
        GameDto gameDto = modelMapper.map(game, GameDto.class);
        Map<String, Object> update = new HashMap<>();
        update.put("type", type);
        update.put("game", gameDto);
        sseEmitters.send(update);
    }

    public void sendGameDelete(String gameId) {
        if (!sseEmitters.hasActiveEmitters()) {
            log.warn("Attempted to send game deletion, but no active SSE connections.");
            return;
        }
        Map<String, Object> update = new HashMap<>();
        update.put("type", "DELETE_GAME");
        update.put("gameId", gameId);
        sseEmitters.send(update);
    }
}