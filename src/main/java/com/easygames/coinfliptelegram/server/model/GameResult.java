package com.easygames.coinfliptelegram.server.model;

import lombok.AllArgsConstructor;

public record GameResult(String coinResult, GameChoice playerChoice, boolean playerWins, long opponentId) {
}