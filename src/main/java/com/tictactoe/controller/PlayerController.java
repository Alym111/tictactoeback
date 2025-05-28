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

    @PostMapping("/register")
    public Boolean addPlayer(@RequestBody Player player) {
        return service.registerPlayer(player);
    }
    @PostMapping("/login")
    public Boolean login(@RequestBody Player player){
        return service.login(player);
    }
}
