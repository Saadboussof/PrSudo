package com.xoudouqi.model;

import java.util.*;

public class Board {
    public static final int ROWS = 9;
    public static final int COLS = 7;
    
    private final TerrainType[][] terrain;
    private final Map<Position, Piece> pieces;

    public Board() {
        this.terrain = new TerrainType[ROWS][COLS];
        this.pieces = new HashMap<>();
        initializeTerrain();
        initializePieces();
    }

    private void initializeTerrain() {
        // Initialize all terrain as normal first
        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLS; col++) {
                terrain[row][col] = TerrainType.NORMAL;
            }
        }

        // Set up water (rivers) - rows 3,4,5 columns 1,2,4,5
        for (int row = 3; row <= 5; row++) {
            terrain[row][1] = TerrainType.WATER;
            terrain[row][2] = TerrainType.WATER;
            terrain[row][4] = TerrainType.WATER;
            terrain[row][5] = TerrainType.WATER;
        }

        // Set up traps around sanctuaries
        // Player 1 traps (around top sanctuary)
        terrain[0][2] = TerrainType.TRAP;
        terrain[1][3] = TerrainType.TRAP;
        terrain[0][4] = TerrainType.TRAP;

        // Player 2 traps (around bottom sanctuary)
        terrain[8][2] = TerrainType.TRAP;
        terrain[7][3] = TerrainType.TRAP;
        terrain[8][4] = TerrainType.TRAP;

        // Set up sanctuaries
        terrain[0][3] = TerrainType.SANCTUARY; // Player 1 sanctuary
        terrain[8][3] = TerrainType.SANCTUARY; // Player 2 sanctuary
    }

    private void initializePieces() {
        pieces.put(new Position(0, 0), new Piece(AnimalType.LION, Player.PLAYER1, new Position(0, 0)));
        pieces.put(new Position(0, 6), new Piece(AnimalType.TIGER, Player.PLAYER1, new Position(0, 6)));
        pieces.put(new Position(1, 1), new Piece(AnimalType.CHIEN, Player.PLAYER1, new Position(1, 1)));
        pieces.put(new Position(1, 5), new Piece(AnimalType.CHAT, Player.PLAYER1, new Position(1, 5)));
        pieces.put(new Position(2, 0), new Piece(AnimalType.ELEPHANT, Player.PLAYER1, new Position(2, 0)));
        pieces.put(new Position(2, 2), new Piece(AnimalType.LOUP, Player.PLAYER1, new Position(2, 2)));
        pieces.put(new Position(2, 4), new Piece(AnimalType.PANTHER, Player.PLAYER1, new Position(2, 4)));
        pieces.put(new Position(2, 6), new Piece(AnimalType.RAT, Player.PLAYER1, new Position(2, 6)));

        pieces.put(new Position(8, 0), new Piece(AnimalType.TIGER, Player.PLAYER2, new Position(8, 0)));
        pieces.put(new Position(8, 6), new Piece(AnimalType.LION, Player.PLAYER2, new Position(8, 6)));
        pieces.put(new Position(7, 1), new Piece(AnimalType.CHAT, Player.PLAYER2, new Position(7, 1)));
        pieces.put(new Position(7, 5), new Piece(AnimalType.CHIEN, Player.PLAYER2, new Position(7, 5)));
        pieces.put(new Position(6, 0), new Piece(AnimalType.RAT, Player.PLAYER2, new Position(6, 0)));
        pieces.put(new Position(6, 2), new Piece(AnimalType.PANTHER, Player.PLAYER2, new Position(6, 2)));
        pieces.put(new Position(6, 4), new Piece(AnimalType.LOUP, Player.PLAYER2, new Position(6, 4)));
        pieces.put(new Position(6, 6), new Piece(AnimalType.ELEPHANT, Player.PLAYER2, new Position(6, 6)));
    }

    public TerrainType getTerrainAt(Position pos) {
        if (!pos.isValid()) return null;
        return terrain[pos.getRow()][pos.getCol()];
    }

    public Piece getPieceAt(Position pos) {
        return pieces.get(pos);
    }

    public void setPieceAt(Position pos, Piece piece) {
        if (piece == null) {
            pieces.remove(pos);
        } else {
            pieces.put(pos, piece);
            piece.setPosition(pos);
        }
    }

    public Collection<Piece> getAllPieces() {
        return pieces.values();
    }

    public Collection<Piece> getPiecesForPlayer(Player player) {
        return pieces.values().stream()
                .filter(piece -> piece.getOwner() == player)
                .collect(ArrayList::new, (list, piece) -> list.add(piece), (list1, list2) -> list1.addAll(list2));
    }

    public boolean isValidMove(Position from, Position to, Player currentPlayer) {
        if (!from.isValid() || !to.isValid()) return false;
        
        Piece piece = getPieceAt(from);
        if (piece == null || piece.getOwner() != currentPlayer) return false;

        if (from.equals(to)) return false;

        int rowDiff = Math.abs(to.getRow() - from.getRow());
        int colDiff = Math.abs(to.getCol() - from.getCol());
        
        boolean isAdjacentMove = (rowDiff == 1 && colDiff == 0) || (rowDiff == 0 && colDiff == 1);
        
        boolean isJumpMove = false;
        if (piece.getType().canCrossWater()) {
            isJumpMove = isValidJumpMove(from, to);
        }

        if (!isAdjacentMove && !isJumpMove) return false;

        TerrainType toTerrain = getTerrainAt(to);
        if (toTerrain == TerrainType.WATER && !piece.getType().canSwimInWater()) {
            return false;
        }

        Piece targetPiece = getPieceAt(to);
        if (targetPiece != null) {
            if (targetPiece.getOwner() == currentPlayer) {
                return false; 
            }
            
            // Check if in sanctuary
            if (getTerrainAt(to) == TerrainType.SANCTUARY) {
                return false; 
            }
            
            return canCapturePiece(piece, targetPiece, to);
        }

        return true;
    }

    private boolean isValidJumpMove(Position from, Position to) {
        int rowDiff = to.getRow() - from.getRow();
        int colDiff = to.getCol() - from.getCol();
        
        if (rowDiff != 0 && colDiff != 0) return false;
        
        if (rowDiff == 0) { // Horizontal jump
            int startCol = Math.min(from.getCol(), to.getCol()) + 1;
            int endCol = Math.max(from.getCol(), to.getCol()) - 1;
            for (int col = startCol; col <= endCol; col++) {
                Position pos = new Position(from.getRow(), col);
                if (getTerrainAt(pos) != TerrainType.WATER) return false;
                if (getPieceAt(pos) != null) return false; // Can't jump over pieces
            }
        } else { // Vertical jump
            int startRow = Math.min(from.getRow(), to.getRow()) + 1;
            int endRow = Math.max(from.getRow(), to.getRow()) - 1;
            for (int row = startRow; row <= endRow; row++) {
                Position pos = new Position(row, from.getCol());
                if (getTerrainAt(pos) != TerrainType.WATER) return false;
                if (getPieceAt(pos) != null) return false; // Can't jump over pieces
            }
        }
        
        return true;
    }

    private boolean canCapturePiece(Piece attacker, Piece defender, Position defenderPosition) {
        TerrainType terrain = getTerrainAt(defenderPosition);
        
        // If defender is in a trap, any piece can capture it
        if (terrain == TerrainType.TRAP && defender.getOwner() != attacker.getOwner()) {
            return true;
        }
        
        return attacker.canCapture(defender);
    }

    public void movePiece(Position from, Position to) {
        Piece piece = getPieceAt(from);
        if (piece == null) return;
        
        // Remove piece from old position
        pieces.remove(from);
        
        // Remove any piece at destination (capture)
        pieces.remove(to);
        
        // Place piece at new position
        setPieceAt(to, piece);
    }

    public boolean hasWinner() {
        // Check if any player has reached opponent's sanctuary
        Piece p1Sanctuary = getPieceAt(new Position(8, 3));
        Piece p2Sanctuary = getPieceAt(new Position(0, 3));
        
        if (p1Sanctuary != null && p1Sanctuary.getOwner() == Player.PLAYER1) {
            return true;
        }
        if (p2Sanctuary != null && p2Sanctuary.getOwner() == Player.PLAYER2) {
            return true;
        }
        
        // Check if any player has no pieces left
        boolean p1HasPieces = pieces.values().stream().anyMatch(p -> p.getOwner() == Player.PLAYER1);
        boolean p2HasPieces = pieces.values().stream().anyMatch(p -> p.getOwner() == Player.PLAYER2);
        
        return !p1HasPieces || !p2HasPieces;
    }

    public Player getWinner() {
        if (!hasWinner()) return null;
        
        // Check sanctuary win
        Piece p1Sanctuary = getPieceAt(new Position(8, 3));
        Piece p2Sanctuary = getPieceAt(new Position(0, 3));
        
        if (p1Sanctuary != null && p1Sanctuary.getOwner() == Player.PLAYER1) {
            return Player.PLAYER1;
        }
        if (p2Sanctuary != null && p2Sanctuary.getOwner() == Player.PLAYER2) {
            return Player.PLAYER2;
        }
        
        // Check elimination win
        boolean p1HasPieces = pieces.values().stream().anyMatch(p -> p.getOwner() == Player.PLAYER1);
        boolean p2HasPieces = pieces.values().stream().anyMatch(p -> p.getOwner() == Player.PLAYER2);
        
        if (!p1HasPieces) return Player.PLAYER2;
        if (!p2HasPieces) return Player.PLAYER1;
        
        return null;
    }
}
