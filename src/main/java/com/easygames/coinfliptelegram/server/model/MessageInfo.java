package com.easygames.coinfliptelegram.server.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MessageInfo {
    Set<Long> chatIds = new HashSet<>();
    int messageId;
}