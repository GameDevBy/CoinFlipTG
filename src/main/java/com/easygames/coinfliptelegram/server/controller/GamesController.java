package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.service.GameService;
import com.easygames.coinfliptelegram.server.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@AllArgsConstructor
public class GamesController {

    private final GameService gameService;
    private final UserService userService;

    @PostMapping()
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameRequest request) {
        GameDto game = gameService.createGame(request);
        return ResponseEntity.ok(game);
    }

    @PutMapping("{telegramId}/{gameId}")
    public ResponseEntity<GameDto> joinmGame(@PathVariable long telegramId, @PathVariable String gameId) {
        UserDto user = userService.getUser(telegramId);
        GameDto game = gameService.getGame(gameId);
        GameDto updatedGame = gameService.joinGame(user, game);
        return ResponseEntity.ok(updatedGame);
    }

    @GetMapping()
    public ResponseEntity<List<GameDto>> getGames() {
        List<GameDto> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }

    @DeleteMapping("{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable String gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
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
