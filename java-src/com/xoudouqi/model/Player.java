package com.xoudouqi.model;

/**
 * Represents a player in the game
 */
public enum Player {
    PLAYER1("Player 1", "P1"),
    PLAYER2("Player 2", "P2");

    private final String name;
    private final String shortName;

    Player(String name, String shortName) {
        this.name = name;
        this.shortName = shortName;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public Player getOpponent() {
        return this == PLAYER1 ? PLAYER2 : PLAYER1;
    }
}
