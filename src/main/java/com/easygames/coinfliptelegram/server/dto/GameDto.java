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
public class GameDto {
    private String id;
    private String gameCode;
    private Long initiatorId;
    private String initiatorUsername;
    private Long opponentId;
    private String opponentUsername;
    private int bet;
    private GameChoice initiatorChoice;
    private GameResult result;
    private LocalDateTime createdAt;
    private LocalDateTime playedAt;
    private GameState state;
}
