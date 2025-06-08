package com.xoudouqi.database;

import java.time.LocalDateTime;

/**
 * Represents a player account in the database
 */
public class PlayerAccount {
    private int id;
    private String username;
    private String password;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    public PlayerAccount() {}

    public PlayerAccount(String username, String password) {
        this.username = username;
        this.password = password;
        this.gamesPlayed = 0;
        this.gamesWon = 0;
        this.gamesLost = 0;
        this.createdAt = LocalDateTime.now();
        this.lastLogin = LocalDateTime.now();
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }

    public int getGamesWon() { return gamesWon; }
    public void setGamesWon(int gamesWon) { this.gamesWon = gamesWon; }

    public int getGamesLost() { return gamesLost; }
    public void setGamesLost(int gamesLost) { this.gamesLost = gamesLost; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public double getWinRate() {
        if (gamesPlayed == 0) return 0.0;
        return (double) gamesWon / gamesPlayed * 100.0;
    }

    public void incrementGamesPlayed() {
        this.gamesPlayed++;
    }

    public void incrementGamesWon() {
        this.gamesWon++;
    }

    public void incrementGamesLost() {
        this.gamesLost++;
    }

    @Override
    public String toString() {
        return String.format("Player: %s | Games: %d | Won: %d | Lost: %d | Win Rate: %.1f%%",
                username, gamesPlayed, gamesWon, gamesLost, getWinRate());
    }
}
