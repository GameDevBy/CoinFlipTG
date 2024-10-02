package com.easygames.coinfliptelegram.server.dao;

import com.easygames.coinfliptelegram.server.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends MongoRepository<Game, String> {
Optional<Game> findByGameCode(String gameCode);
}
