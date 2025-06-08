package com.xoudouqi;

import com.xoudouqi.model.*;
import com.xoudouqi.database.*;
import java.util.*;

/**
 * Main game engine for Xou Dou Qi
 * Handles game logic, player turns, and win conditions
 */
public class GameEngine {
    private Board board;
    private Player currentPlayer;
    private List<Move> moveHistory;
    private DatabaseManager dbManager;
    private PlayerAccount player1Account;
    private PlayerAccount player2Account;
    private int gameHistoryId;
    private boolean gameEnded;
    
    public GameEngine(DatabaseManager dbManager) {
        this.board = new Board();
        this.currentPlayer = Player.PLAYER1;
        this.moveHistory = new ArrayList<>();
        this.dbManager = dbManager;
        this.gameEnded = false;
    }
    
    public void setPlayers(PlayerAccount player1, PlayerAccount player2) {
        this.player1Account = player1;
        this.player2Account = player2;
        this.gameHistoryId = dbManager.createGameHistory(player1.getUsername(), player2.getUsername());
    }
    
    public Board getBoard() {
        return board;
    }
    
    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    
    public List<Move> getMoveHistory() {
        return moveHistory;
    }
    
    public boolean isGameEnded() {
        return gameEnded;
    }
    
    public PlayerAccount getCurrentPlayerAccount() {
        return currentPlayer == Player.PLAYER1 ? player1Account : player2Account;
    }
    
    public String getCurrentPlayerName() {
        PlayerAccount account = getCurrentPlayerAccount();
        return account != null ? account.getUsername() : currentPlayer.getName();
    }
    
    /**
     * Attempts to make a move from one position to another
     * Returns true if the move was successful, false otherwise
     */
    public boolean makeMove(Position from, Position to) {
        if (gameEnded) {
            return false;
        }
        
        if (!board.isValidMove(from, to, currentPlayer)) {
            return false;
        }
        
        Piece movingPiece = board.getPieceAt(from);
        Piece capturedPiece = board.getPieceAt(to);
        
        // Execute the move
        board.movePiece(from, to);
        
        // Record the move
        Move move = new Move(from, to, currentPlayer, movingPiece, capturedPiece);
        moveHistory.add(move);
        
        // Check for game end conditions
        if (board.hasWinner()) {
            endGame();
            return true;
        }
        
        // Switch to next player
        currentPlayer = currentPlayer.getOpponent();
        
        return true;
    }
    
    /**
     * Attempts to make a move using string notation (e.g., "a1 b2")
     */
    public boolean makeMove(String moveStr) {
        try {
            String[] parts = moveStr.trim().split("\\s+");
            if (parts.length != 2) {
                return false;
            }
            
            Position from = Position.fromString(parts[0]);
            Position to = Position.fromString(parts[1]);
            
            return makeMove(from, to);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Gets all valid moves for the current player
     */
    public List<String> getValidMoves() {
        List<String> validMoves = new ArrayList<>();
        Collection<Piece> playerPieces = board.getPiecesForPlayer(currentPlayer);
        
        for (Piece piece : playerPieces) {
            Position from = piece.getPosition();
            
            // Check all possible destinations
            for (int row = 0; row < Board.ROWS; row++) {
                for (int col = 0; col < Board.COLS; col++) {
                    Position to = new Position(row, col);
                    if (board.isValidMove(from, to, currentPlayer)) {
                        validMoves.add(from.toStringNotation() + " " + to.toStringNotation());
                    }
                }
            }
        }
        
        return validMoves;
    }
    
    /**
     * Gets all pieces that can move for the current player
     */
    public List<Piece> getMovablePieces() {
        List<Piece> movablePieces = new ArrayList<>();
        Collection<Piece> playerPieces = board.getPiecesForPlayer(currentPlayer);
        
        for (Piece piece : playerPieces) {
            Position from = piece.getPosition();
            boolean canMove = false;
            
            // Check if this piece has any valid moves
            for (int row = 0; row < Board.ROWS && !canMove; row++) {
                for (int col = 0; col < Board.COLS && !canMove; col++) {
                    Position to = new Position(row, col);
                    if (board.isValidMove(from, to, currentPlayer)) {
                        canMove = true;
                    }
                }
            }
            
            if (canMove) {
                movablePieces.add(piece);
            }
        }
        
        return movablePieces;
    }
    
    private void endGame() {
        gameEnded = true;
        Player winner = board.getWinner();
        
        // Update database
        if (player1Account != null && player2Account != null) {
            String winnerUsername = null;
            
            if (winner == Player.PLAYER1) {
                dbManager.updatePlayerStats(player1Account.getUsername(), true);
                dbManager.updatePlayerStats(player2Account.getUsername(), false);
                winnerUsername = player1Account.getUsername();
            } else if (winner == Player.PLAYER2) {
                dbManager.updatePlayerStats(player1Account.getUsername(), false);
                dbManager.updatePlayerStats(player2Account.getUsername(), true);
                winnerUsername = player2Account.getUsername();
            }
            
            dbManager.endGame(gameHistoryId, winnerUsername, "WIN", moveHistory.size());
        }
    }
    
    public Player getWinner() {
        if (!gameEnded) return null;
        return board.getWinner();
    }
    
    public String getWinnerName() {
        Player winner = getWinner();
        if (winner == null) return null;
        
        if (winner == Player.PLAYER1 && player1Account != null) {
            return player1Account.getUsername();
        } else if (winner == Player.PLAYER2 && player2Account != null) {
            return player2Account.getUsername();
        }
        
        return winner.getName();
    }
    
    /**
     * Forfeits the game for the current player
     */
    public void forfeit() {
        if (gameEnded) return;
        
        gameEnded = true;
        Player winner = currentPlayer.getOpponent();
        
        // Update database
        if (player1Account != null && player2Account != null) {
            String winnerUsername = null;
            
            if (winner == Player.PLAYER1) {
                dbManager.updatePlayerStats(player1Account.getUsername(), true);
                dbManager.updatePlayerStats(player2Account.getUsername(), false);
                winnerUsername = player1Account.getUsername();
            } else {
                dbManager.updatePlayerStats(player1Account.getUsername(), false);
                dbManager.updatePlayerStats(player2Account.getUsername(), true);
                winnerUsername = player2Account.getUsername();
            }
            
            dbManager.endGame(gameHistoryId, winnerUsername, "FORFEIT", moveHistory.size());
        }
    }
    
    /**
     * Gets a summary of the current game state
     */
    public String getGameSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== Game Summary ===\n");
        
        if (player1Account != null && player2Account != null) {
            sb.append("Player 1: ").append(player1Account.getUsername()).append("\n");
            sb.append("Player 2: ").append(player2Account.getUsername()).append("\n");
        }
        
        sb.append("Current Turn: ").append(getCurrentPlayerName()).append("\n");
        sb.append("Total Moves: ").append(moveHistory.size()).append("\n");
        
        if (gameEnded) {
            sb.append("Game Status: ENDED\n");
            sb.append("Winner: ").append(getWinnerName()).append("\n");
        } else {
            sb.append("Game Status: IN PROGRESS\n");
        }
        
        return sb.toString();
    }
}
