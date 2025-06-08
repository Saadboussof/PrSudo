package com.xoudouqi.model;

/**
 * Represents a position on the game board
 */
public class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isValid() {
        return row >= 0 && row < 9 && col >= 0 && col < 7;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return row * 7 + col;
    }

    @Override
    public String toString() {
        return "(" + row + "," + col + ")";
    }

    /**
     * Creates a position from string notation like "a1", "b2", etc.
     */
    public static Position fromString(String pos) {
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException("Invalid position format: " + pos);
        }
        char colChar = Character.toLowerCase(pos.charAt(0));
        char rowChar = pos.charAt(1);
        
        if (colChar < 'a' || colChar > 'g' || rowChar < '1' || rowChar > '9') {
            throw new IllegalArgumentException("Invalid position: " + pos);
        }
        
        int col = colChar - 'a';
        int row = rowChar - '1';
        return new Position(row, col);
    }

    /**
     * Converts position to string notation like "a1", "b2", etc.
     */
    public String toStringNotation() {
        char colChar = (char) ('a' + col);
        char rowChar = (char) ('1' + row);
        return "" + colChar + rowChar;
    }
}
