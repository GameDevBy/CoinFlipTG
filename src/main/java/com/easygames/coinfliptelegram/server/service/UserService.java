package com.easygames.coinfliptelegram.server.service;

import com.easygames.coinfliptelegram.server.dao.UserRepository;
import com.easygames.coinfliptelegram.server.dto.Score;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.model.User;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    public UserDto saveUser(long telegramId, String username) {
        Optional<User> user = userRepository.findByTelegramId(telegramId);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            Score score = new Score();
            score.setFlipkyBalance(500);
            User newUser = User.builder()
                    .telegramId(telegramId)
                    .username(username)
                    .score(score)
                    .build();
            return modelMapper.map(
                    userRepository.save(newUser),
                    UserDto.class);
        }
    }

    public UserDto getUser(Long telegramId, String username) {
        Optional<User> optionalUser = userRepository.findByTelegramId(telegramId);
        if (optionalUser.isPresent()) {
            return modelMapper.map(optionalUser.get(), UserDto.class);
        } else {
            return saveUser(telegramId, username);
        }
    }

    public UserDto getUser(Long telegramId) {
        Optional<User> optionalUser = userRepository.findByTelegramId(telegramId);
        if (optionalUser.isPresent()) {
            return modelMapper.map(optionalUser, UserDto.class);
        } else {
            return null;
        }
    }

    public Optional<Long> getChatIdByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
        return Optional.ofNullable(user.getTelegramId());
    }

    public Optional<String> getUsernameByTelegramId(long telegramId) throws Exception {
        User user = userRepository.findByTelegramId(telegramId).orElseThrow(() -> new Exception("User not found"));
        return Optional.ofNullable(user.getUsername());
    }
}