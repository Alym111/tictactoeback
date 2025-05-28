package com.tictactoe.service;

import com.tictactoe.model.Game;
import com.tictactoe.model.GameStatus;
import com.tictactoe.model.Player;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());
    private Map<String, Game> activeGames = new HashMap<>();

    public Game startNewGame(Player player) {
        if (player == null || player.getUsername() == null) {
            LOGGER.severe("Invalid player data");
            throw new IllegalArgumentException("Player data is invalid");
        }

        LOGGER.info("Starting new game for player: " + player.getUsername());
        Game game = new Game();
        game.setPlayer1(player);
        game.setCurrentPlayer(player.getUsername());
        game.setStatus(GameStatus.WAITING);
        activeGames.put(game.getGameId(), game);
        return game;
    }

    public List<Game> getAvailableGames() {
        return activeGames.values().stream()
                .filter(game -> game.getPlayer2() == null)
                .collect(Collectors.toList());
    }

    public Game processMove(Game game) {
        Game activeGame = activeGames.get(game.getGameId());
        if (activeGame == null) {
            LOGGER.severe("Game not found: " + game.getGameId());
            throw new IllegalStateException("Game not found");
        }

        activeGame.setBoard(game.getBoard());
        if (activeGame.getStatus() != GameStatus.IN_PROGRESS) {
            LOGGER.warning("Game is not in progress: " + game.getGameId());
            throw new IllegalStateException("Game is not in progress");
        }

        if (!game.getCurrentPlayer().equals(activeGame.getCurrentPlayer())) {
            LOGGER.warning("Not your turn: " + game.getCurrentPlayer());
            throw new IllegalStateException("Not your turn");
        }

        if (checkWinner(activeGame.getBoard(), 'X')) {
            activeGame.setWinner(activeGame.getPlayer1().getUsername());
            activeGame.setStatus(GameStatus.FINISHED);
        } else if (checkWinner(activeGame.getBoard(), 'O')) {
            activeGame.setWinner(activeGame.getPlayer2().getUsername());
            activeGame.setStatus(GameStatus.FINISHED);
        }
        activeGame.setMoveCount(activeGame.getMoveCount() + 1);
        if (activeGame.getMoveCount() >= 9 && activeGame.getWinner() == null) {
            activeGame.setStatus(GameStatus.FINISHED);
        }

        activeGame.setCurrentPlayer(
                activeGame.getCurrentPlayer().equals(activeGame.getPlayer1().getUsername()) ?
                        activeGame.getPlayer2().getUsername() :
                        activeGame.getPlayer1().getUsername()
        );

        return activeGame;
    }

    public Game joinGame(String gameId, Player player) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            LOGGER.severe("Game not found: " + gameId);
            throw new IllegalStateException("Game not found");
        }
        if (game.getPlayer2() != null) {
            LOGGER.warning("Game is full: " + gameId);
            throw new IllegalStateException("Game is full");
        }
        game.setPlayer2(player);
        game.setStatus(GameStatus.IN_PROGRESS);
        return game;
    }

    public Game restartGame(String gameId) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            LOGGER.severe("Game not found: " + gameId);
            throw new IllegalStateException("Game not found");
        }
        game.setBoard(new char[9]);
        game.setWinner(null);
        game.setMoveCount(0);
        game.setCurrentPlayer(game.getPlayer1().getUsername());
        game.setStatus(GameStatus.IN_PROGRESS);
        return game;
    }

    private boolean checkWinner(char[] board, char symbol) {
        int[][] winCombinations = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8}, // rows
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8}, // columns
                {0, 4, 8}, {2, 4, 6}             // diagonals
        };

        for (int[] combination : winCombinations) {
            if (board[combination[0]] == symbol &&
                    board[combination[1]] == symbol &&
                    board[combination[2]] == symbol) {
                return true;
            }
        }
        return false;
    }
}