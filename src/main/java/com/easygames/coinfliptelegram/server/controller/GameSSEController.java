package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.config.SseEmitters;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.model.Game;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/sse/games")
@AllArgsConstructor
public class GameSSEController {

    private final SseEmitters sseEmitters;
    private final ModelMapper modelMapper;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamGames() {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        sseEmitters.add(emitter);
        emitter.onCompletion(() -> sseEmitters.remove(emitter));
        emitter.onTimeout(() -> sseEmitters.remove(emitter));
        return emitter;
    }

    // Call this method when a game is created, updated, or deleted
    public void sendGameUpdate(String type, Game game) {
        GameDto gameDto = modelMapper.map(game, GameDto.class);
        Map<String, Object> update = new HashMap<>();
        update.put("type", type);
        update.put("game", gameDto);
        sseEmitters.send(update);
    }

    public void sendGameDelete(String gameId) {
        Map<String, Object> update = new HashMap<>();
        update.put("type", "DELETE_GAME");
        update.put("gameId", gameId);
        sseEmitters.send(update);
    }
}