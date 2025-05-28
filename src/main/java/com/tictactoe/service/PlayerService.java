package com.tictactoe.service;

import com.tictactoe.model.Player;
import com.tictactoe.repository.PlayerRepo;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class PlayerService {
    private final PlayerRepo repository;
    private final PasswordEncoder passwordEncoder;

    public PlayerService(PlayerRepo repo, PasswordEncoder encoder) {
        this.repository = repo;
        this.passwordEncoder = encoder;
    }

    public Player register(Player player) {
        if (repository.findByUsername(player.getUsername()).isPresent()) {
            throw new RuntimeException("Username taken");
        }

        player.setPassword(passwordEncoder.encode(player.getPassword()));
        player.setToken(generateToken());
        return repository.save(player);
    }

    public String login(String username, String password) {
        Player player = repository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(password, player.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        player.setToken(generateToken());
        player.setOnline(true);
        return repository.save(player).getToken();
    }

    public void logout(String token) {
        findByToken(token).ifPresent(player -> {
            player.setOnline(false);
            repository.save(player);
        });
    }

    public void deleteAccount(String token) {
        Player player = findByToken(token)
                .orElseThrow(() -> new RuntimeException("Invalid token"));
        repository.delete(player);
    }

    public Optional<Player> findByToken(String token) {
        return repository.findByToken(token);
    }

    private String generateToken() {
        return UUID.randomUUID().toString();
    }
}
