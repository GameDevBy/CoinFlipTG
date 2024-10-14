package com.easygames.coinfliptelegram.server.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Builder
public class User {
    @Id
    private String id;
    private Long telegramId;
    private String username;
    private Score score;

}
