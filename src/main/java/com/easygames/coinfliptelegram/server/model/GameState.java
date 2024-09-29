package com.easygames.coinfliptelegram.server.model;

public enum GameState {
    WAITING_FOR_OPPONENT,
    WAITING_FOR_BET,
    ENTERING_CUSTOM_BET,
    IN_PROGRESS,
    FINISHED,
}