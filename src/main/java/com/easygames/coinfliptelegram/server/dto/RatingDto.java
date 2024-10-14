package com.easygames.coinfliptelegram.server.dto;

import com.easygames.coinfliptelegram.server.model.GameChoice;
import com.easygames.coinfliptelegram.server.model.GameResult;
import com.easygames.coinfliptelegram.server.model.GameState;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RatingDto {
    private int rank;
    private String username;
    private double rating;
    private int playedGames;
    private double winRate;
}
