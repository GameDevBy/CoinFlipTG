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

    public UserDto saveUser(long chatId, String username) {
        Optional<User> user = userRepository.findByTelegramId(chatId);
        if (user.isPresent()) {
            return modelMapper.map(user.get(), UserDto.class);
        } else {
            Score score = new Score();
            score.setFlipkyBalance(500);
            User newUser = User.builder()
                    .telegramId(chatId)
                    .username(username)
                    .score(score)
                    .build();
            return modelMapper.map(
                    userRepository.save(newUser),
                    UserDto.class);
        }
    }

    public UserDto getUser(Long id, String username) {
        Optional<User> optionalUser = userRepository.findByTelegramId(id);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            Score score = user.getScore();
            return modelMapper.map(user, UserDto.class);
        } else {
            return saveUser(id, username);
        }
    }

    public Optional<Long> getChatIdByUsername(String username) throws Exception {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new Exception("User not found"));
        return Optional.ofNullable(user.getTelegramId());
    }

    public Optional<String> getUsernameById(long chatId) throws Exception {
        User user = userRepository.findByTelegramId(chatId).orElseThrow(() -> new Exception("User not found"));
        return Optional.ofNullable(user.getUsername());
    }
}