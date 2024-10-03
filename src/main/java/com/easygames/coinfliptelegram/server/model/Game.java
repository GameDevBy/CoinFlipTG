package com.easygames.coinfliptelegram.server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "games")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@EqualsAndHashCode(of = {"id"})
public class Game {
    @Id
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