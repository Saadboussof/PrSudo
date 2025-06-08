package com.xoudouqi.model;

import java.time.LocalDateTime;

public class Move {
    private final Position from;
    private final Position to;
    private final Player player;
    private final Piece piece;
    private final Piece capturedPiece;
    private final LocalDateTime timestamp;

    public Move(Position from, Position to, Player player, Piece piece, Piece capturedPiece) {
        this.from = from;
        this.to = to;
        this.player = player;
        this.piece = piece;
        this.capturedPiece = capturedPiece;
        this.timestamp = LocalDateTime.now();
    }

    public Position getFrom() {
        return from;
    }

    public Position getTo() {
        return to;
    }

    public Player getPlayer() {
        return player;
    }

    public Piece getPiece() {
        return piece;
    }

    public Piece getCapturedPiece() {
        return capturedPiece;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public boolean isCapture() {
        return capturedPiece != null;
    }

    @Override
    public String toString() {
        String moveStr = piece.getType().getName() + " " + from.toStringNotation() + "->" + to.toStringNotation();
        if (isCapture()) {
            moveStr += " (captures " + capturedPiece.getType().getName() + ")";
        }
        return moveStr;
    }
}
