package com.easygames.coinfliptelegram.server.model;

import lombok.AllArgsConstructor;

public record GameResult(GameChoice coinResult, boolean isInitiatorWins) {
}