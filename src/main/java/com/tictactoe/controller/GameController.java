package com.tictactoe.controller;

import com.tictactoe.JwtUtil;
import com.tictactoe.model.Game;
import com.tictactoe.model.Player;
import com.tictactoe.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/games")
public class GameController {

    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());

    @Autowired
    private GameService gameService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Player player, HttpServletRequest request) {
        try {
            // Проверяем JWT
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                LOGGER.warning("No valid Authorization header");
                return ResponseEntity.status(401).build();
            }

            String token = authHeader.substring(7);
            String username = jwtUtil.getUsernameFromToken(token);
            if (!jwtUtil.validateToken(token)) {
                LOGGER.warning("Invalid JWT token");
                return ResponseEntity.status(401).build();
            }

            // Проверяем, совпадает ли username в теле запроса с токеном
            if (!username.equals(player.getUsername())) {
                LOGGER.warning("Username mismatch: token=" + username + ", request=" + player.getUsername());
                return ResponseEntity.status(403).build();
            }

            LOGGER.info("Creating game for player: " + username);
            Game game = gameService.startNewGame(player);
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            LOGGER.severe("Error creating game: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public List<Game> getAvailableGames() {
        LOGGER.info("Fetching available games");
        return gameService.getAvailableGames();
    }

    @MessageMapping("/game/start")
    @SendTo("/topic/game/{gameId}")
    public Game startGame(Player player) {
        LOGGER.info("Starting WebSocket game for player: " + player.getUsername());
        return gameService.startNewGame(player);
    }

    @MessageMapping("/game/join/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public Game joinGame(@DestinationVariable String gameId, Player player) {
        LOGGER.info("Player " + player.getUsername() + " joining game " + gameId);
        return gameService.joinGame(gameId, player);
    }

    @MessageMapping("/game/move")
    @SendTo("/topic/game/{gameId}")
    public Game makeMove(Game game) {
        LOGGER.info("Processing move for game " + game.getGameId());
        return gameService.processMove(game);
    }

    @MessageMapping("/game/restart/{gameId}")
    @SendTo("/topic/game/{gameId}")
    public Game restartGame(@DestinationVariable String gameId) {
        LOGGER.info("Restarting game " + gameId);
        return gameService.restartGame(gameId);
    }
}