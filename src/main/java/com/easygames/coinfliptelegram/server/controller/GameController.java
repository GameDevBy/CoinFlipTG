package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.model.GameResult;
import com.easygames.coinfliptelegram.server.model.GameState;
import com.easygames.coinfliptelegram.server.service.GameService;
import com.easygames.coinfliptelegram.server.service.UserService;
import com.easygames.coinfliptelegram.server.tgbot.CoinFlipTGBot;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/games")
@AllArgsConstructor
public class GameController {
    public static final double PERCENT_OF_WIN = 0.5;

    private final GameService gameService;
    private final UserService userService;
    private final CoinFlipTGBot bot;

    @PostMapping()
    public ResponseEntity<GameDto> createGame(@RequestBody CreateGameRequest request) {
        GameDto game = gameService.createGame(request);
        return ResponseEntity.ok(game);
    }

    @PutMapping("{userId}/{gameId}/join")
    public ResponseEntity<GameDto> joinGame(@PathVariable String userId, @PathVariable String gameId) {
        try {
            UserDto user = userService.getUser(userId);
            GameDto game = gameService.getGame(gameId);
            if (user.getScore().getFlipkyBalance()< game.getBet()){
                return null;
            }
            if (game.getState().equals(GameState.WAITING_FOR_OPPONENT)) {
                GameDto updatedGame = gameService.joinGame(user, game);
                bot.joinGameMessage(game.getInitiatorId(), game.getOpponentUsername(), game.getBet());
                return ResponseEntity.ok(updatedGame);
            } else {
                return null;
            }
        } catch (TelegramApiException e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @PutMapping("{gameId}/cancel")
    public ResponseEntity<GameDto> cancelGame(@PathVariable String gameId) {
        try {
            GameDto game = gameService.getGame(gameId);
            String opponentUsername = game.getOpponentUsername();
            GameDto updatedGame = gameService.cancelGame(game);
            bot.cancelGameMessage(game.getInitiatorId(), opponentUsername, game.getBet(), game.getInitiatorChoice());
            return ResponseEntity.ok(updatedGame);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    @PutMapping("{gameId}/flip")
    public ResponseEntity<GameResult> flipCoin(@PathVariable String gameId) {
        GameDto game = gameService.getGame(gameId);
        GameDto updatedGame = gameService.flipCoin(game, PERCENT_OF_WIN, false);
        bot.sendResultMessages(updatedGame);
        return ResponseEntity.ok(updatedGame.getResult());
    }

    @GetMapping()
    public ResponseEntity<List<GameDto>> getGames() {
        List<GameDto> games = gameService.getGames();
        return ResponseEntity.ok(games);
    }

    @GetMapping("{userId}/history")
    public ResponseEntity<List<GameDto>> getPlayedGames(@PathVariable String userId) {
        UserDto user = userService.getUser(userId);
        List<GameDto> games = gameService.getPlayedGames(user.getTelegramId());
        return ResponseEntity.ok(games);
    }

    @DeleteMapping("{gameId}")
    public ResponseEntity<?> deleteGame(@PathVariable String gameId) {
        gameService.deleteGame(gameId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("bot/{userId}")
    public ResponseEntity<GameResult> botGame(@PathVariable String userId, @RequestBody CreateGameRequest gameRequest) {
        UserDto user = userService.getUser(userId);
        if (user.getScore().getFlipkyBalance() < gameRequest.getBet()) {
            return null;
        }
        UserDto coinBot = userService.getUser(bot.getBotId(), bot.getBotUsername());
        GameDto game = gameService.gameVsBot(user, coinBot, gameRequest);
        return ResponseEntity.ok(game.getResult());
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
