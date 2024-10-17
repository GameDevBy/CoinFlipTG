package com.easygames.coinfliptelegram.server.service;

import com.easygames.coinfliptelegram.server.controller.GameSSEController;
import com.easygames.coinfliptelegram.server.dao.GameRepository;
import com.easygames.coinfliptelegram.server.dao.UserRepository;
import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.model.Score;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.model.Game;
import com.easygames.coinfliptelegram.server.model.GameChoice;
import com.easygames.coinfliptelegram.server.model.GameResult;
import com.easygames.coinfliptelegram.server.model.GameState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.jetbrains.annotations.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@AllArgsConstructor
public class GameService {

    public static final double PERCENT_OF_WIN_VS_BOT = 0.3;
    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private GameSSEController gameSSEController;
    @Value("${BOT.NAME}")
    private String botUsername;

    public List<GameDto> getGames() {
        return gameRepository.findAll().stream()
                .filter(game -> GameState.FINISHED != game.getState())
                .sorted((Comparator.comparing(Game::getCreatedAt)))
                .map(game -> modelMapper.map(game, GameDto.class))
                .toList();
    }

    public List<GameDto> getPlayedGames(Long userId) {
        return gameRepository.findPlayedGames(userId, GameState.FINISHED)
                .map(game -> {
                    GameDto gameDto = modelMapper.map(game, GameDto.class);

                    // Replace bot username with "CoinFlip_Bot"
                    if (botUsername.equals(gameDto.getInitiatorUsername())) {
                        gameDto.setInitiatorUsername("CoinFlip_Bot");
                    }
                    if (botUsername.equals(gameDto.getOpponentUsername())) {
                        gameDto.setOpponentUsername("CoinFlip_Bot");
                    }
                    // If the user is the opponent, swap usernames
                    if (userId.equals(game.getOpponentId())) {
                        String tempUsername = gameDto.getInitiatorUsername();
                        gameDto.setOpponentUsername(tempUsername);
                    }
                    return gameDto;
                })
                .sorted((Comparator.comparing(GameDto::getPlayedAt)))
                .toList();
    }

    public GameDto createGame(CreateGameRequest createGameRequest) {
        try {
            String gameCode = UUID.randomUUID().toString().substring(0, 8);
            Game createdGame = Game.builder()
                    .gameCode(gameCode)
                    .initiatorId(createGameRequest.getInitiatorId())
                    .initiatorUsername(createGameRequest.getInitiatorUsername())
                    .createdAt(LocalDateTime.now())
                    .bet(createGameRequest.getBet())
                    .initiatorChoice(createGameRequest.getInitiatorChoice())
                    .state(GameState.WAITING_FOR_OPPONENT)
                    .build();

            userRepository.findByTelegramId(createGameRequest.getInitiatorId()).ifPresent(user -> {
                Score score = user.getScore();
                if (score.getFlipkyBalance() < createdGame.getBet()) {
                    try {
                        throw new BadRequestException("Not enough flipky");
                    } catch (BadRequestException e) {
                        throw new RuntimeException(e);
                    }
                }
                score.setFlipkyBalance(score.getFlipkyBalance() - createdGame.getBet());
                userRepository.save(user);
            });
            Game saved = gameRepository.save(createdGame);
            gameSSEController.sendGameUpdate("NEW_GAME", createdGame);
            return modelMapper.map(saved, GameDto.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public GameDto joinGame(UserDto userDto, GameDto gameDto) {
        gameDto.setOpponentId(userDto.getTelegramId());
        gameDto.setOpponentUsername(userDto.getUsername());
        gameDto.setState(GameState.IN_PROGRESS);
        Game game = gameRepository.save(modelMapper.map(gameDto, Game.class));
        gameSSEController.sendGameUpdate("UPDATE_GAME", game);

        return gameDto;
    }

    public GameDto getGame(String id) {
        return modelMapper.map(gameRepository.findById(id), GameDto.class);
    }

    public GameDto getGameByGameCode(String gameCode) {
        Optional<Game> game = gameRepository.findByGameCode(gameCode);
        if (game.isPresent()) {
            return modelMapper.map(game, GameDto.class);
        } else {
            return null;
        }
    }

    public boolean isGameFinished(String gameCode) {
        GameDto game = getGameByGameCode(gameCode);
        return game == null || game.getState() == GameState.FINISHED;
    }

    public void deleteGame(String gameId) {
        gameRepository.findById(gameId).ifPresent(
                game -> {
                    userRepository.findByTelegramId(game.getInitiatorId()).ifPresent(
                            user -> {
                                Score score = user.getScore();
                                score.setFlipkyBalance(score.getFlipkyBalance() + game.getBet());
                                user.setScore(score);
                                userRepository.save(user);
                            }
                    );
                }
        );
        gameRepository.deleteById(gameId);
        gameSSEController.sendGameDelete(gameId);
    }

    public GameDto cancelGame(GameDto gameDto) {
        gameDto.setOpponentId(null);
        gameDto.setOpponentUsername(null);
        gameDto.setState(GameState.WAITING_FOR_OPPONENT);
        gameRepository.save(modelMapper.map(gameDto, Game.class));
        return gameDto;
    }

    public GameDto flipCoin(GameDto game, double percentOfWin, boolean isPlayVSBot) {
        double randomNumber = Math.random();
        GameChoice userChoice = game.getInitiatorChoice();
        GameChoice result = randomNumber < percentOfWin
                ? userChoice
                : (userChoice.equals(GameChoice.HEADS)
                ? GameChoice.TAILS : GameChoice.HEADS);
        boolean initiatorWins = (userChoice.equals(result));
        return storeGameResult(game, result, initiatorWins, isPlayVSBot);
    }

    @NotNull
    private GameDto storeGameResult(GameDto game, GameChoice result, boolean initiatorWins, boolean isGameVSBot) {
        GameResult gameResult = new GameResult(result, initiatorWins);
        game.setResult(gameResult);
        game.setState(GameState.FINISHED);
        game.setPlayedAt(LocalDateTime.now());
        gameRepository.save(modelMapper.map(game, Game.class));

        int bet = game.getBet();
        int scoreChange = initiatorWins
                ? (isGameVSBot ? bet : bet * 2)
                : (isGameVSBot ? -bet : 0);

        updateUserScore(game.getInitiatorId(), scoreChange, initiatorWins, bet);
        updateUserScore(game.getOpponentId(), initiatorWins ? -bet : bet, !initiatorWins, bet);
        return game;
    }

    public GameDto gameVsBot(UserDto user, UserDto coinBot, CreateGameRequest gameRequest) {
        boolean isPlayVSBot = true;
        String gameCode = UUID.randomUUID().toString().substring(0, 8);
        GameChoice userChoice = gameRequest.getInitiatorChoice();
        Game createdGame = Game.builder()
                .gameCode(gameCode)
                .initiatorId(user.getTelegramId())
                .initiatorUsername(user.getUsername())
                .createdAt(LocalDateTime.now())
                .bet(gameRequest.getBet())
                .initiatorChoice(userChoice)
                .opponentId(coinBot.getTelegramId())
                .opponentUsername(coinBot.getUsername())
                .state(GameState.FINISHED)
                .build();

        return flipCoin(modelMapper.map(createdGame, GameDto.class), PERCENT_OF_WIN_VS_BOT, isPlayVSBot);
    }

    private void updateUserScore(Long telegramId, int updatedFlipky, boolean isWin, int bet) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            Score score = user.getScore();
            score.setFlipkyBalance(score.getFlipkyBalance() + updatedFlipky);
            score.setPlayedGames(score.getPlayedGames() + 1);
            if (isWin) {
                score.setWins(score.getWins() + 1);
                score.setTotalWinFlipky(score.getTotalWinFlipky() + bet);
            } else {
                score.setLosses(score.getLosses() + 1);
                score.setTotalLossFlipky(score.getTotalLossFlipky() + bet);
            }
            userRepository.save(user);
        });
    }


}
