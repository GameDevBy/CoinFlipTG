package com.easygames.coinfliptelegram.server.service;

import com.easygames.coinfliptelegram.server.dao.GameRepository;
import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.model.Game;
import com.easygames.coinfliptelegram.server.model.GameState;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;

    public List<GameDto> getGames() {
        return gameRepository.findAll().stream()
                .filter(game -> GameState.FINISHED != game.getState())
                .sorted((Comparator.comparing(Game::getCreatedAt)))
                .map(game -> modelMapper.map(game, GameDto.class))
                .toList();
    }

    public GameDto createGame(CreateGameRequest createGameRequest) {
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

        Game saved = gameRepository.save(createdGame);
        return modelMapper.map(saved, GameDto.class);

    }


    public GameDto joinGame(UserDto userDto, GameDto gameDto) {
        gameDto.setOpponentId(userDto.getTelegramId());
        gameDto.setOpponentUsername(userDto.getUsername());
        gameDto.setState(GameState.IN_PROGRESS);
        gameRepository.save(modelMapper.map(gameDto,Game.class));
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
        gameRepository.deleteById(gameId);
    }
}
