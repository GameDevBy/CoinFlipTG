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

    public static final int WIN_RATE_WEIGHT = 45;
    public static final int WIN_FLIPKY_WEIGHT = 40;
    public static final int PLAYED_GAMES_WEIGHT = 15;
    public static final int MAX_RATED_WIN_AMOUNT = 500;
    public static final double WIN_RATE_SCALING = 100.0;
    public static final double POINTS_PER_GAME = 0.01;
    public static final double BONUS_PER_AMOUNT_GAMES = 0.1;
    public static final double ACHIEVE_AMOUNT_GAMES = 10.0;

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
        int playedGames = score.getPlayedGames();
        double winRate = (double) score.getWins() / Math.max(playedGames, 1);
        double averageFlipkyWin = (double) score.getTotalWinFlipky() / Math.max(score.getWins(), 1);

        // Calculate played games contribution
        double basePoints = playedGames * POINTS_PER_GAME;
        double bonusPoints = Math.floor(playedGames / ACHIEVE_AMOUNT_GAMES) * BONUS_PER_AMOUNT_GAMES;
        double playedGamesContribution = (basePoints + bonusPoints) * PLAYED_GAMES_WEIGHT / 100.0;

        // Normalize average win amount
        double normalizedAverageFlipkyWin = Math.min(averageFlipkyWin / MAX_RATED_WIN_AMOUNT, 1);
        double winFlipkyContribution = normalizedAverageFlipkyWin * WIN_FLIPKY_WEIGHT / 100.0;

        // Apply scaling factor to win rate based on games played
        double winRateScaling = Math.min(playedGames / WIN_RATE_SCALING, 1.0);
        double winRateContribution = winRate * WIN_RATE_WEIGHT * winRateScaling / 100.0;

        // Calculate final rating
        double rating = (winRateContribution + winFlipkyContribution + playedGamesContribution) * 100;

        DecimalFormat df = new DecimalFormat("#");
        return Double.parseDouble(df.format(rating));
    }

    private double calculateWinRate(Score score) {
        double winRate = (double) score.getWins() / Math.max(score.getPlayedGames(), 1);
        DecimalFormat df = new DecimalFormat("#");
        return Double.parseDouble(df.format(winRate * 100));
    }
}