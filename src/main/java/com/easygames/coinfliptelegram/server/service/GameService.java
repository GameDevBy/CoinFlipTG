package com.easygames.coinfliptelegram.server.service;

import com.easygames.coinfliptelegram.server.dao.GameRepository;
import com.easygames.coinfliptelegram.server.dao.UserRepository;
import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.dto.Score;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.model.Game;
import com.easygames.coinfliptelegram.server.model.GameChoice;
import com.easygames.coinfliptelegram.server.model.GameResult;
import com.easygames.coinfliptelegram.server.model.GameState;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
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

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public List<GameDto> getGames() {
        return gameRepository.findAll().stream()
                .filter(game -> GameState.FINISHED != game.getState())
                .sorted((Comparator.comparing(Game::getCreatedAt)))
                .map(game -> modelMapper.map(game, GameDto.class))
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
        gameRepository.save(modelMapper.map(gameDto, Game.class));
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
    }

    public GameDto cancelGame(GameDto gameDto) {
        gameDto.setOpponentId(null);
        gameDto.setOpponentUsername(null);
        gameDto.setState(GameState.WAITING_FOR_OPPONENT);
        gameRepository.save(modelMapper.map(gameDto, Game.class));
        return gameDto;
    }

    public GameDto flipCoin(GameDto game) {
        boolean isHeads = Math.random() < 0.5;
        GameChoice result = isHeads ? GameChoice.HEADS : GameChoice.TAILS;
        GameChoice choice = game.getInitiatorChoice();
        boolean initiatorWins = (choice.equals(result));

        GameResult gameResult = new GameResult(result, initiatorWins);
        game.setResult(gameResult);
        game.setState(GameState.FINISHED);
        gameRepository.save(modelMapper.map(game, Game.class));

        int bet = game.getBet(); // You can make this dynamic based on user input
        updateUserScore(game.getInitiatorId(), initiatorWins ? (bet * 2) : -(bet * 2), initiatorWins);
        updateUserScore(game.getOpponentId(), initiatorWins ? -bet : bet, !initiatorWins);
        return game;
    }

    private void updateUserScore(Long telegramId, int updatedFlipky, boolean isWin) {
        userRepository.findByTelegramId(telegramId).ifPresent(user -> {
            Score score = user.getScore();
            score.setFlipkyBalance(score.getFlipkyBalance() + updatedFlipky);
            score.setPlayedGames(score.getPlayedGames() + 1);
            if (isWin) {
                score.setWins(score.getWins() + 1);
            } else {
                score.setLosses(score.getWins() + 1);
            }
            userRepository.save(user);
        });
    }
}
