package com.easygames.coinfliptelegram.server.service;

import com.easygames.coinfliptelegram.server.dao.GameRepository;
import com.easygames.coinfliptelegram.server.dto.CreateGameRequest;
import com.easygames.coinfliptelegram.server.dto.GameDto;
import com.easygames.coinfliptelegram.server.model.Game;
import com.easygames.coinfliptelegram.server.model.GameState;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final ModelMapper modelMapper;

    public List<GameDto> getGames() {
        return gameRepository.findAll().stream()
                .filter(game -> GameState.WAITING_FOR_OPPONENT == game.getState())
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


    public GameDto joinGame(Long userId, Long gameId) {
        // Join existing game in database
        // Return updated GameDTO object
        return null;
    }

    public GameDto getGame(String id) {
        return modelMapper.map(gameRepository.findById(id), GameDto.class);
    }
}
