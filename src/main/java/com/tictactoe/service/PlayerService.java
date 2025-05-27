package com.tictactoe.service;

import com.tictactoe.model.Player;
import com.tictactoe.repository.PlayerRepo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepo repository;

    public PlayerService(PlayerRepo repository) {
        this.repository = repository;
    }

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public Player addPlayer(Player player) {
        return repository.save(player);
    }
}
