package com.easygames.coinfliptelegram.server.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JoinGameResult {
    public final boolean success;
    public final String message;
    public final long initiatorId;

}