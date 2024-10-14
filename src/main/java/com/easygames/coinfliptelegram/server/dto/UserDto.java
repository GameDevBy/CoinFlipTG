package com.easygames.coinfliptelegram.server.dto;

import com.easygames.coinfliptelegram.server.model.Score;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private String id;
    private Long telegramId;
    private String username;
    private Score score;
}
