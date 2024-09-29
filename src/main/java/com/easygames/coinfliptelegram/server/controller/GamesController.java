package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.service.GameService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@AllArgsConstructor
public class GamesController {

    private final GameService gameService;


    @PostMapping()
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameRequest request) {
        GameDto game = gameService.createGame(request);
        return ResponseEntity.ok(game);
    }

    @GetMapping()
    public ResponseEntity<List<GameDto>> getGames() {
        List<GameDto> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }
//    // WebSocket endpoint to handle player joining
//    @MessageMapping("/game.join")
//    @SendTo("/topic/game/{gameId}")
//    public PlayerJoinedMessage handlePlayerJoin(JoinGameRequest request) {
//        return gameService.joinGame(request);
//    }
//
//    @GetMapping("/game/{id}")
//    public ResponseEntity<GameDto> getGame(@PathVariable Long id) {
//        GameDto game = gameService.getGame(id);
//        return ResponseEntity.ok(game);
//    }

}
