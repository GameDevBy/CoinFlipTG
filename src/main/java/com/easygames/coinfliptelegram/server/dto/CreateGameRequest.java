package com.easygames.coinfliptelegram.server.dto;

import com.easygames.coinfliptelegram.server.model.GameChoice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreateGameRequest {
    private Long initiatorId;
    private String initiatorUsername;
    private int bet;
    private GameChoice initiatorChoice;
}
