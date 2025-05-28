package com.tictactoe.service;

import com.tictactoe.model.Player;
import com.tictactoe.repository.PlayerRepo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepo repository;

    public PlayerService(PlayerRepo repository) {
        this.repository = repository;
    }

    public List<Player> getAllPlayers() {
        return repository.findAll();
    }

    public Boolean registerPlayer(Player player) {
        try {
            repository.save(player);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean login(Player player) {
        Optional<Player> userOptional = repository.findByUsername(player.getUsername());
        if (userOptional.isPresent()) {
            Player user = userOptional.get();
            return user.getPassword().equals(player.getPassword());
        }
        return false;
    }

}
