package com.xoudouqi.database;

import java.time.LocalDateTime;

/**
 * Represents a game record in the database
 */
public class GameHistory {
    private int id;
    private String player1Username;
    private String player2Username;
    private String winner;
    private int totalMoves;
    private LocalDateTime gameStartTime;
    private LocalDateTime gameEndTime;
    private String gameResult; // "WIN", "DRAW", "FORFEIT"

    public GameHistory() {}

    public GameHistory(String player1Username, String player2Username) {
        this.player1Username = player1Username;
        this.player2Username = player2Username;
        this.gameStartTime = LocalDateTime.now();
        this.totalMoves = 0;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getPlayer1Username() { return player1Username; }
    public void setPlayer1Username(String player1Username) { this.player1Username = player1Username; }

    public String getPlayer2Username() { return player2Username; }
    public void setPlayer2Username(String player2Username) { this.player2Username = player2Username; }

    public String getWinner() { return winner; }
    public void setWinner(String winner) { this.winner = winner; }

    public int getTotalMoves() { return totalMoves; }
    public void setTotalMoves(int totalMoves) { this.totalMoves = totalMoves; }

    public LocalDateTime getGameStartTime() { return gameStartTime; }
    public void setGameStartTime(LocalDateTime gameStartTime) { this.gameStartTime = gameStartTime; }

    public LocalDateTime getGameEndTime() { return gameEndTime; }
    public void setGameEndTime(LocalDateTime gameEndTime) { this.gameEndTime = gameEndTime; }

    public String getGameResult() { return gameResult; }
    public void setGameResult(String gameResult) { this.gameResult = gameResult; }

    public void endGame(String winnerUsername, String result) {
        this.winner = winnerUsername;
        this.gameResult = result;
        this.gameEndTime = LocalDateTime.now();
    }

    public void incrementMoves() {
        this.totalMoves++;
    }

    @Override
    public String toString() {
        return String.format("Game: %s vs %s | Winner: %s | Moves: %d | Result: %s",
                player1Username, player2Username, winner, totalMoves, gameResult);
    }
}
