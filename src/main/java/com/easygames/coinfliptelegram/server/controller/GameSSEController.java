package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.config.SseEmitters;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.model.Game;
import com.easygames.coinfliptelegram.server.model.Score;
import com.easygames.coinfliptelegram.server.model.SseAction;
import com.easygames.coinfliptelegram.server.model.SseType;
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
        try {
            SseEmitter emitter = sseEmitters.create(userId);
            // Send an initial event to establish the connection
            emitter.send(SseEmitter.event().id("connect").name("connect").data("Connected").build());
            log.info("New SSE connection established for user {}. Total active connections: {}",
                    userId, sseEmitters.getActiveEmittersCount());
            return emitter;
        } catch (IOException e) {
            log.error("Error sending initial SSE event to user {}", userId, e);
            sseEmitters.remove(userId);
            return null;
        }
    }

    @PostMapping("/close/{userId}")
    public ResponseEntity<Void> closeSSEConnection(@PathVariable String userId) {
        sseEmitters.remove(userId);
        return ResponseEntity.ok().build();
    }

    // Call this method when a game is created, updated, or deleted
    public void sendGameUpdate(SseAction action, Game game) {
        try {
            if (!sseEmitters.hasActiveEmitters()) {
                log.warn("Attempted to send game update, but no active SSE connections.");
                return;
            }
            GameDto gameDto = modelMapper.map(game, GameDto.class);
            Map<String, Object> update = new HashMap<>();
            update.put("type", SseType.GAME);
            update.put("action", action);
            update.put("game", gameDto);
            sseEmitters.send(update);
        } catch (Exception e) {
            log.error("Error sending game update: ", e);
        }
    }

    public void sendScoreUpdate(SseAction action, Score score, String userId) {
        try {
            if (!sseEmitters.hasActiveEmitters()) {
                log.warn("Attempted to send score update, but no active SSE connections.");
                return;
            }
            Map<String, Object> update = new HashMap<>();
            update.put("type", SseType.SCORE);
            update.put("action", action);
            update.put("score", score);
            sseEmitters.sendToUser(userId, update);
        } catch (Exception e) {
            log.error("Error sending score update: ", e);
        }
    }

    public void sendGameDelete(String gameId) {
        try {
            if (!sseEmitters.hasActiveEmitters()) {
                log.warn("Attempted to send game deletion, but no active SSE connections.");
                return;
            }
            Map<String, Object> update = new HashMap<>();
            update.put("type", SseType.GAME);
            update.put("action", SseAction.GAME_DELETE);
            update.put("gameId", gameId);
            sseEmitters.send(update);
        } catch (Exception e) {
            log.error("Error deleting game: ", e);
        }
    }
}