package com.easygames.coinfliptelegram.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Score {
    private int wins;
    private int losses;
    private int playedGames;
    private int flipkyBalance;
    private int totalWinFlipky;
    private int totalLossFlipky;
}
