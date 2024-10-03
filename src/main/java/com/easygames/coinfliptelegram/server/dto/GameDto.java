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
    private long initiatorId;
    private String initiatorUsername;
    private long opponentId;
    private String opponentUsername;
    private int bet;
    private GameChoice initiatorChoice;
    private GameResult result;
    private LocalDateTime createdAt;
    private GameState state;
}
