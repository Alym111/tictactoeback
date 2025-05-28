package com.tictactoe.controller;

import com.tictactoe.model.Player;
import com.tictactoe.service.PlayerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/players")
public class PlayerController {
    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @PostMapping("/register")
    public Player register(@RequestBody Player player) {
        return service.register(player);
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        return service.login(username, password);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader("Authorization") String token) {
        service.logout(token);
    }

    @DeleteMapping
    public void deleteAccount(@RequestHeader("Authorization") String token) {
        service.deleteAccount(token);
    }

    @GetMapping("/validate")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        return service.findByToken(token).isPresent();
    }
}
