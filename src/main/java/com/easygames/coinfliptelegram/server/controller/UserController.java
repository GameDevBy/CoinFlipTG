package com.easygames.coinfliptelegram.server.controller;

import com.easygames.coinfliptelegram.server.dto.LoginRequest;
import com.easygames.coinfliptelegram.server.dto.UserDto;
import com.easygames.coinfliptelegram.server.service.GameService;
import com.easygames.coinfliptelegram.server.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final GameService gameService;

    @PostMapping("")
    public ResponseEntity<UserDto> getUserData(@RequestBody LoginRequest loginRequest) {
        log.info("Received GET request for user with id: {}", loginRequest.getTelegramId());
        UserDto userData = userService.getUser( loginRequest.getTelegramId(),  loginRequest.getUsername());
        return ResponseEntity.ok(userData);
    }



}
