package com.tictactoe.service;

import com.tictactoe.model.*;
import com.tictactoe.repository.GameResultRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

@Service
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());
    private Map<String, Game> activeGames = new HashMap<>();

    @Autowired
    private GameResultRepo gameResultRepo;

    public Game startNewGame(Player player) {
        if (player == null || player.getUsername() == null) {
            LOGGER.severe("Invalid player data");
            throw new IllegalArgumentException("Player data is invalid");
        }

        for (Game game : activeGames.values()) {
            if (game.getPlayer1() != null
                    && game.getPlayer1().getUsername().equals(player.getUsername())
                    && game.getPlayer2() == null
                    && game.getStatus() == GameStatus.WAITING) {
                LOGGER.info("Found existing waiting game for player: " + player.getUsername());
                return game;
            }
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

        if (checkWinner(activeGame.getBoard(), "X")) {
            activeGame.setWinner(activeGame.getPlayer1().getUsername());
            activeGame.setStatus(GameStatus.FINISHED);
        } else if (checkWinner(activeGame.getBoard(), "O")) {
            activeGame.setWinner(activeGame.getPlayer2().getUsername());
            activeGame.setStatus(GameStatus.FINISHED);
        }
        activeGame.setMoveCount(activeGame.getMoveCount() + 1);
        if (activeGame.getMoveCount() >= 9 && activeGame.getWinner() == null) {
            activeGame.setStatus(GameStatus.FINISHED);
        }
        if (activeGame.getStatus() == GameStatus.FINISHED) {
            saveGameResult(activeGame);
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

    public Game handleRematchRequest(String gameId, RematchRequest request) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            LOGGER.warning("Rematch: Game not found: " + gameId);
            return null;
        }

        if (game.getPlayer1() != null && request.getUsername().equals(game.getPlayer1().getUsername())) {
            game.setPlayer1WantsRematch(request.isAgree());
        }
        if (game.getPlayer2() != null && request.getUsername().equals(game.getPlayer2().getUsername())) {
            game.setPlayer2WantsRematch(request.isAgree());
        }

        if (!request.isAgree()) {
            game.setPlayer1WantsRematch(false);
            game.setPlayer2WantsRematch(false);
            game.setStatus(GameStatus.REJECTED);
            LOGGER.info("Rematch rejected by " + request.getUsername());
            return game;
        }

        if (game.isPlayer1WantsRematch() && game.isPlayer2WantsRematch()) {
            restartGame(gameId);
            game.setPlayer1WantsRematch(false);
            game.setPlayer2WantsRematch(false);
            LOGGER.info("Rematch accepted by both players, restarting game " + gameId);
        }

        return game;
    }
    public PlayerStatistics getPlayerStatistics(String username) {
        List<GameResult> results = gameResultRepo.findAllByPlayer(username);

        int totalGames = results.size();
        int wins = 0;
        int draws = 0;
        int losses = 0;

        int maxWinStreak = 0;
        int currentWinStreak = 0;
        int tempStreak = 0;
        results.sort(Comparator.comparing(GameResult::getFinishedAt));

        for (GameResult gr : results) {
            boolean isWin = gr.getWinner() != null && gr.getWinner().equals(username);
            boolean isDraw = gr.getWinner() == null;
            if (isWin) {
                wins++;
                tempStreak++;
                if (tempStreak > maxWinStreak) {
                    maxWinStreak = tempStreak;
                }
            } else {
                if (isDraw) draws++;
                else losses++;
                tempStreak = 0;
            }
        }
        currentWinStreak = 0;
        for (int i = results.size() - 1; i >= 0; i--) {
            GameResult gr = results.get(i);
            boolean isWin = gr.getWinner() != null && gr.getWinner().equals(username);
            if (isWin) currentWinStreak++;
            else break;
        }

        return new PlayerStatistics(username, totalGames, wins, losses, draws, currentWinStreak, maxWinStreak);
    }
    public List<PlayerStatistics> getAllPlayersStatistics() {
        List<String> usernames = gameResultRepo.findAllDistinctPlayers();

        List<PlayerStatistics> statsList = new ArrayList<>();
        for (String username : usernames) {
            PlayerStatistics stats = getPlayerStatistics(username);
            statsList.add(stats);
        }

        statsList.sort((a, b) -> Integer.compare(b.getMaxWinStreak(), a.getMaxWinStreak()));

        return statsList;
    }

    public Game leaveGame(String gameId, Player player) {
        Game game = activeGames.get(gameId);
        if (game == null) return null;

        boolean isPlayer1 = game.getPlayer1() != null && player.getUsername().equals(game.getPlayer1().getUsername());
        boolean isPlayer2 = game.getPlayer2() != null && player.getUsername().equals(game.getPlayer2().getUsername());

        if (isPlayer1) {
            activeGames.remove(gameId);
            LOGGER.info("leaveGame: Удалили игру " + gameId + ", осталось: " + activeGames.keySet());
            return null;
        }
        if (isPlayer2) {
            if (game.getStatus() == GameStatus.FINISHED) {
                game.setPlayer2(null);
                game.setBoard(new String[9]);
                game.setWinner(null);
                game.setMoveCount(0);
                game.setCurrentPlayer(game.getPlayer1().getUsername());
                game.setStatus(GameStatus.WAITING);
                game.setPlayer1WantsRematch(false);
                game.setPlayer2WantsRematch(false);
                LOGGER.info("leaveGame: player2 вышел после окончания, игра сброшена " + gameId);
                return game;
            } else {
                game.setPlayer2(null);
                game.setStatus(GameStatus.WAITING);
                LOGGER.info("leaveGame: player2 вышел из игры " + gameId);
                return game;
            }
        }
        return game;
    }

    public Game restartGame(String gameId) {
        Game game = activeGames.get(gameId);
        if (game == null) {
            LOGGER.severe("Game not found: " + gameId);
            throw new IllegalStateException("Game not found");
        }
        game.setBoard(new String[9]);
        game.setWinner(null);
        game.setMoveCount(0);
        game.setCurrentPlayer(game.getPlayer1().getUsername());
        game.setStatus(GameStatus.IN_PROGRESS);
        game.setPlayer1WantsRematch(false);
        game.setPlayer2WantsRematch(false);
        return game;
    }
    public void saveGameResult(Game game) {
        GameResult result = new GameResult();
        result.setGameId(game.getGameId());
        result.setPlayer1(game.getPlayer1() != null ? game.getPlayer1().getUsername() : null);
        result.setPlayer2(game.getPlayer2() != null ? game.getPlayer2().getUsername() : null);
        result.setWinner(game.getWinner());
        result.setFinishedAt(java.time.LocalDateTime.now());
        gameResultRepo.save(result);
    }

    private boolean checkWinner(String[] board, String symbol) {
        int[][] winCombinations = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };

        for (int[] combination : winCombinations) {
            if (symbol.equals(board[combination[0]])
                    && symbol.equals(board[combination[1]])
                    && symbol.equals(board[combination[2]])) {
                return true;
            }
        }
        return false;
    }
}