package com.tictactoe.model;

public class PlayerStatistics {
    private String username;
    private int totalGames;
    private int wins;
    private int losses;
    private int draws;
    private int currentWinStreak;
    private int maxWinStreak;

    public PlayerStatistics() {}

    public PlayerStatistics(String username, int totalGames, int wins, int losses, int draws, int currentWinStreak, int maxWinStreak) {
        this.username = username;
        this.totalGames = totalGames;
        this.wins = wins;
        this.losses = losses;
        this.draws = draws;
        this.currentWinStreak = currentWinStreak;
        this.maxWinStreak = maxWinStreak;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getTotalGames() { return totalGames; }
    public void setTotalGames(int totalGames) { this.totalGames = totalGames; }

    public int getWins() { return wins; }
    public void setWins(int wins) { this.wins = wins; }

    public int getLosses() { return losses; }
    public void setLosses(int losses) { this.losses = losses; }

    public int getDraws() { return draws; }
    public void setDraws(int draws) { this.draws = draws; }

    public int getCurrentWinStreak() {
        return currentWinStreak;
    }

    public void setCurrentWinStreak(int currentWinStreak) {
        this.currentWinStreak = currentWinStreak;
    }

    public int getMaxWinStreak() {
        return maxWinStreak;
    }

    public void setMaxWinStreak(int maxWinStreak) {
        this.maxWinStreak = maxWinStreak;
    }
}
