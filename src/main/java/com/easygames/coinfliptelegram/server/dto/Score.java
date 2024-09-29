package com.easygames.coinfliptelegram.server.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Score {
    private int winsAmount;
    private int lossAmount;
    private int playedGamesAmount;
    private int flipkyBalance;

}
