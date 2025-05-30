package com.tictactoe.controller;

import com.tictactoe.JwtUtil;
import com.tictactoe.model.Game;
import com.tictactoe.model.Player;
import com.tictactoe.service.GameService;
import com.tictactoe.model.RematchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody Player player, HttpServletRequest request) {
        try {
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

            if (!username.equals(player.getUsername())) {
                LOGGER.warning("Username mismatch: token=" + username + ", request=" + player.getUsername());
                return ResponseEntity.status(403).build();
            }

            LOGGER.info("Creating game for player: " + username);
            Game game = gameService.startNewGame(player);
            messagingTemplate.convertAndSend("/topic/games", gameService.getAvailableGames());
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
    @MessageMapping("/game/leave/{gameId}")
    public void leaveGame(@DestinationVariable String gameId, @RequestBody Player player) {
        LOGGER.info("Player " + player.getUsername() + " is leaving game " + gameId);
        Game game = gameService.leaveGame(gameId, player);
        // Оповести обоих игроков о новом состоянии игры
        messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
        // Рассылка нового списка доступных игр в лобби
        messagingTemplate.convertAndSend("/topic/games", gameService.getAvailableGames());
    }

    @MessageMapping("/game/start")
    public void startGame(Player player) {
        LOGGER.info("Starting WebSocket game for player: " + player.getUsername());
        Game game = gameService.startNewGame(player);
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), game);
    }

    @MessageMapping("/game/join/{gameId}")
    public void joinGame(@DestinationVariable String gameId, Player player) {
        LOGGER.info("Player " + player.getUsername() + " joining game " + gameId);
        Game game = gameService.joinGame(gameId, player);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
        messagingTemplate.convertAndSend("/topic/games", gameService.getAvailableGames());
    }

    @MessageMapping("/game/move")
    public void makeMove(Game game) {
        LOGGER.info("Processing move for game " + game.getGameId());
        Game updated = gameService.processMove(game);
        messagingTemplate.convertAndSend("/topic/game/" + game.getGameId(), updated);
    }

    @MessageMapping("/game/restart/{gameId}")
    public void restartGame(@DestinationVariable String gameId) {
        LOGGER.info("Restarting game " + gameId);
        Game game = gameService.restartGame(gameId);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
    }

    // --- Новый endpoint для rematch ---
    @MessageMapping("/game/rematch/{gameId}")
    public void rematch(@DestinationVariable String gameId, RematchRequest request) {
        LOGGER.info("Rematch request from " + request.getUsername() + " for game " + gameId);
        Game game = gameService.handleRematchRequest(gameId, request);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, game);
    }
}