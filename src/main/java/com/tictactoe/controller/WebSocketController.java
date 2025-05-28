package com.tictactoe.controller;

import com.tictactoe.model.Player;
import com.tictactoe.service.PlayerService;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@Controller
public class WebSocketController {
    private final PlayerService playerService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(PlayerService service,
                               SimpMessagingTemplate template) {
        this.playerService = service;
        this.messagingTemplate = template;
    }

    @MessageMapping("/auth")
    public void authenticate(@Header("Authorization") String token) {
        playerService.findByToken(token).ifPresentOrElse(
                player -> {
                    player.setOnline(true);
                    playerService.save(player);
                    messagingTemplate.convertAndSendToUser(
                            token,
                            "/queue/auth",
                            Map.of("status", "AUTH_OK", "username", player.getUsername())
                    );
                },
                () -> messagingTemplate.convertAndSendToUser(
                        token,
                        "/queue/auth",
                        Map.of("status", "AUTH_FAILED")
                )
        );
    }
}
