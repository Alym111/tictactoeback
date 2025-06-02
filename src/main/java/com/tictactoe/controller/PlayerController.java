package com.tictactoe.controller;

import com.tictactoe.model.LoginCountByDateDto;
import com.tictactoe.model.Player;
import com.tictactoe.service.PlayerService;
import com.tictactoe.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/players")
public class PlayerController {

    private static final Logger LOGGER = Logger.getLogger(PlayerController.class.getName());
    private final PlayerService playerService;
    private final JwtUtil jwtUtil;

    public PlayerController(PlayerService playerService, JwtUtil jwtUtil) {
        this.playerService = playerService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<Boolean> register(@RequestBody Player player) {
        try {
            boolean isRegistered = playerService.registerPlayer(player);
            LOGGER.info("Player registration attempt: " + player.getUsername() + ", success: " + isRegistered);
            return ResponseEntity.ok(isRegistered);
        } catch (Exception e) {
            LOGGER.severe("Error registering player: " + e.getMessage());
            return ResponseEntity.badRequest().body(false);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Player player) {
        try {
            if (playerService.login(player)) {
                String token = jwtUtil.generateToken(player.getUsername());
                LOGGER.info("Login successful for: " + player.getUsername() + ", token: " + token);
                return ResponseEntity.ok(token);
            }
            LOGGER.warning("Invalid login attempt for: " + player.getUsername());
            return ResponseEntity.status(401).body("Invalid credentials");
        } catch (Exception e) {
            LOGGER.severe("Login error: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Login failed");
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Player> getCurrentPlayer(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                LOGGER.warning("No valid Authorization header");
                return ResponseEntity.status(401).body(null);
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (!jwtUtil.validateToken(token)) {
                LOGGER.warning("Invalid JWT token");
                return ResponseEntity.status(401).body(null);
            }

            Player player = new Player();
            player.setUsername(username);
            LOGGER.info("Fetched current player: " + username);
            return ResponseEntity.ok(player);
        } catch (Exception e) {
            LOGGER.severe("Error fetching current player: " + e.getMessage());
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/all")
    public List<Player> getAllPlayers() {
        LOGGER.info("Fetching all players");
        return playerService.getAllPlayers();
    }
    @GetMapping("/logins/count-by-date")
    public ResponseEntity<List<LoginCountByDateDto>> getLoginCountsByDate() {
        List<LoginCountByDateDto> response = playerService.getLoginCountsByDate();
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(player -> ResponseEntity.ok(player))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlayerById(@PathVariable Long id) {
        boolean deleted = playerService.deletePlayerById(id);
        if (deleted) {
            LOGGER.info("Deleted player with id: " + id);
            return ResponseEntity.noContent().build();
        } else {
            LOGGER.warning("Player not found for deletion with id: " + id);
            return ResponseEntity.notFound().build();
        }
    }
}