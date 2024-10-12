package com.easygames.coinfliptelegram.server.dao;

import com.easygames.coinfliptelegram.server.model.Game;
import com.easygames.coinfliptelegram.server.model.GameState;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
    Optional<Game> findByGameCode(String gameCode);
    @Query("{'$or': [{'initiatorId': ?0}, {'opponentId': ?0}], 'state': ?1}")
    Stream<Game> findPlayedGames(Long userId, GameState state);

}
