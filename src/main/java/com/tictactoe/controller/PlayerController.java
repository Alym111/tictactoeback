package com.tictactoe.controller;

import com.tictactoe.model.Player;
import com.tictactoe.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService service;

    public PlayerController(PlayerService service) {
        this.service = service;
    }

    @GetMapping("/all")
    public List<Player> getAllPlayers() {
        return service.getAllPlayers();
    }

    @PostMapping("/add")
    public Player addPlayer(@RequestBody Player player) {
        return service.addPlayer(player);
    }
}
