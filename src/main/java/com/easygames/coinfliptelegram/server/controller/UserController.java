package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.service.GameService;
import com.easygames.coinfliptelegram.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final GameService gameService;

    @GetMapping("{telegramId}/{username}")
    public ResponseEntity<UserDto> getUserData(@PathVariable Long telegramId, @PathVariable String username) {
        log.info("Received GET request for user with id: {}", telegramId);
        UserDto userData = userService.getUser(telegramId, username);
        return ResponseEntity.ok(userData);
    }



}
