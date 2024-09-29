package com.easygames.coinfliptelegram.server.dao;

import com.easygames.coinfliptelegram.server.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByTelegramId(Long telegramId);
    Optional<User> findByUsername(String username);
}
