package com.xoudouqi.model;

/**
 * Represents different types of terrain on the game board
 */
public enum TerrainType {
    NORMAL("·", "Normal terrain"),
    WATER("~", "River - only certain animals can cross"),
    TRAP("T", "Trap - weakens enemy pieces"),
    SANCTUARY("S", "Sanctuary - safe zone, pieces cannot be captured here");

    private final String symbol;
    private final String description;

    TerrainType(String symbol, String description) {
        this.symbol = symbol;
        this.description = description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getDescription() {
        return description;
    }
}
