package com.tictactoe.model;

import java.time.LocalDateTime;

public class Game {
    private String gameId;
    private Player player1;
    private Player player2;
    private char[] board = new char[9];
    private String currentPlayer;
    private String winner;
    private GameStatus status = GameStatus.WAITING;
    private LocalDateTime createdAt = LocalDateTime.now();
    private int moveCount = 0;
    private boolean player1WantsRematch = false;
    private boolean player2WantsRematch = false;
    // Constructors, getters and setters
    public Game() {
        this.gameId = java.util.UUID.randomUUID().toString();
    }
    public boolean isPlayer1WantsRematch() { return player1WantsRematch; }
    public void setPlayer1WantsRematch(boolean value) { this.player1WantsRematch = value; }
    public boolean isPlayer2WantsRematch() { return player2WantsRematch; }
    public void setPlayer2WantsRematch(boolean value) { this.player2WantsRematch = value; }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Player getPlayer1() {
        return player1;
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public Player getPlayer2() {
        return player2;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public String[] getBoard() {
        String[] arr = new String[board.length];
        for (int i = 0; i < board.length; i++) {
            arr[i] = (board[i] == '\0') ? null : String.valueOf(board[i]);
        }
        return arr;
    }

    public void setBoard(String[] arr) {
        for (int i = 0; i < board.length; i++) {
            board[i] = (arr[i] == null || arr[i].isEmpty()) ? '\0' : arr[i].charAt(0);
        }
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
