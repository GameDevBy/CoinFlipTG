package com.easygames.coinfliptelegram.server.service;

import com.easygames.coinfliptelegram.server.dao.UserRepository;
import com.easygames.coinfliptelegram.server.dto.RatingDto;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.model.Score;
import com.easygames.coinfliptelegram.server.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Slf4j
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
            User user = optionalUser.get();
            if (user.getUsername().equals(username)) {
                return modelMapper.map(user, UserDto.class);
            } else {
                log.info("User: {} change username to: {}", user.getUsername(), username);
                user.setUsername(username);
                return modelMapper.map(userRepository.save(user), UserDto.class);
            }
        } else {
            return saveUser(telegramId, username);
        }
    }

    public UserDto getUser(String userId) {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            return modelMapper.map(optionalUser, UserDto.class);
        } else {
            return null;
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

    public List<RatingDto> getRatings(Long botId) {
        List<User> users = userRepository.findAll();
        List<RatingDto> ratingDtos = users.stream()
                .filter(user -> !user.getTelegramId().equals(botId))
                .map(user -> new RatingDto(
                        0, // Rank will be set later
                        user.getUsername(),
                        calculateRating(user.getScore()),
                        user.getScore().getPlayedGames(),
                        calculateWinRate(user.getScore())
                ))
                .sorted((a, b) -> Double.compare(b.getRating(), a.getRating())) // Sort by rating descending
                .toList();
        // Set ranks
        for (int i = 0; i < ratingDtos.size(); i++) {
            ratingDtos.get(i).setRank(i + 1);
        }
        return ratingDtos;
    }

    private double calculateRating(Score score) {
        double winRate = (double) score.getWins() / Math.max(score.getPlayedGames(), 1);
        double averageWin = (double) score.getTotalWinFlipky() / Math.max(score.getWins(), 1);
        double playedGamesBonus = Math.min((double) score.getPlayedGames() / 100, 1);
        double rating = (winRate * 60) + (averageWin * 0.2) + (playedGamesBonus * 20);
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(rating));
    }

    private double calculateWinRate(Score score) {
        double winRate = (double) (score.getWins() / Math.max(score.getPlayedGames(), 1) * 100);
        DecimalFormat df = new DecimalFormat("#.##");
        return Double.parseDouble(df.format(winRate));
    }
}