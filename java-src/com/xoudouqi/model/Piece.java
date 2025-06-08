package com.xoudouqi.model;

public class Piece {
    private final AnimalType type;
    private final Player owner;
    private Position position;

    public Piece(AnimalType type, Player owner, Position position) {
        this.type = type;
        this.owner = owner;
        this.position = position;
    }

    public AnimalType getType() {
        return type;
    }

    public Player getOwner() {
        return owner;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public boolean canCapture(Piece target) {
        if (target == null || target.owner == this.owner) {
            return false;
        }
        return this.type.canCapture(target.type);
    }

    @Override
    public String toString() {
        String symbol = type.getSymbol();
        return owner == Player.PLAYER1 ? symbol.toUpperCase() : symbol.toLowerCase();
    }    public String getDisplaySymbol() {
        String base = getShortName();
        return owner == Player.PLAYER1 ? base + "1" : base + "2";
    }

    private String getShortName() {
        switch (type) {
            case ELEPHANT: return "E";
            case LION: return "L";
            case TIGER: return "T";
            case PANTHER: return "P";
            case CHIEN: return "D";
            case LOUP: return "W";
            case CHAT: return "C";
            case RAT: return "R";
            default: return "?";
        }
    }
}
