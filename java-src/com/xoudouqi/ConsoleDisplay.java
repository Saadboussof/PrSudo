package com.xoudouqi;

import com.xoudouqi.model.*;
import java.util.Collection;
import java.util.List;

public class ConsoleDisplay {
    
    /**
     * Displays the game board in a formatted way
     */
    public static void displayBoard(Board board) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                XOU DOU QI BOARD");
        System.out.println("=".repeat(50));
        
        // Column headers
        System.out.print("    ");
        for (char c = 'a'; c <= 'g'; c++) {
            System.out.printf(" %c  ", c);
        }
        System.out.println();
        
        // Board rows
        for (int row = 0; row < Board.ROWS; row++) {
            System.out.printf(" %d  ", row + 1);
            
            for (int col = 0; col < Board.COLS; col++) {
                Position pos = new Position(row, col);
                Piece piece = board.getPieceAt(pos);
                TerrainType terrain = board.getTerrainAt(pos);
                
                String cell = formatCell(piece, terrain);
                System.out.print(cell);
            }
            System.out.println();
        }
        
        System.out.println("=".repeat(50));
        displayLegend();
    }
    
    private static String formatCell(Piece piece, TerrainType terrain) {
        if (piece != null) {
            String symbol = getPieceSymbol(piece);
            return String.format("[%s]", symbol);
        } else {
            String terrainSymbol = getTerrainSymbol(terrain);
            return String.format(" %s  ", terrainSymbol);
        }
    }
    
    private static String getPieceSymbol(Piece piece) {
        String base;
        switch (piece.getType()) {
            case ELEPHANT: base = "E"; break;
            case LION: base = "L"; break;
            case TIGER: base = "T"; break;
            case PANTHER: base = "P"; break;
            case CHIEN: base = "D"; break;
            case LOUP: base = "W"; break;
            case CHAT: base = "C"; break;
            case RAT: base = "R"; break;
            default: base = "?"; break;
        }
        
        return piece.getOwner() == Player.PLAYER1 ? base + "1" : base + "2";
    }
    
    private static String getTerrainSymbol(TerrainType terrain) {
        switch (terrain) {
            case WATER: return "~~";
            case TRAP: return "XX";
            case SANCTUARY: return "##";
            default: return "··";
        }
    }
    
    private static void displayLegend() {
        System.out.println("LEGEND:");
        System.out.println("Pieces: E=Elephant, L=Lion, T=Tiger, P=Panther");
        System.out.println("        D=Dog, W=Wolf, C=Cat, R=Rat");
        System.out.println("        1=Player1, 2=Player2");
        System.out.println("Terrain: ·· = Normal, ~~ = Water, XX = Trap, ## = Sanctuary");
        System.out.println();
    }
    
    /**
     * Displays game information
     */
    public static void displayGameInfo(GameEngine engine) {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("GAME INFORMATION");
        System.out.println("-".repeat(30));
        System.out.println("Current Player: " + engine.getCurrentPlayerName());
        System.out.println("Move Count: " + engine.getMoveHistory().size());
        
        // Show player pieces count
        Collection<Piece> p1Pieces = engine.getBoard().getPiecesForPlayer(Player.PLAYER1);
        Collection<Piece> p2Pieces = engine.getBoard().getPiecesForPlayer(Player.PLAYER2);
        
        System.out.println("Player 1 pieces: " + p1Pieces.size());
        System.out.println("Player 2 pieces: " + p2Pieces.size());
        System.out.println("-".repeat(30));
    }
    
    /**
     * Displays recent moves
     */
    public static void displayRecentMoves(GameEngine engine, int count) {
        List<Move> moves = engine.getMoveHistory();
        if (moves.isEmpty()) {
            System.out.println("No moves yet.");
            return;
        }
        
        System.out.println("\nRECENT MOVES:");
        System.out.println("-".repeat(40));
        
        int start = Math.max(0, moves.size() - count);
        for (int i = start; i < moves.size(); i++) {
            Move move = moves.get(i);
            System.out.printf("%d. %s: %s\n", 
                i + 1, move.getPlayer().getName(), move.toString());
        }
        System.out.println("-".repeat(40));
    }
    
    /**
     * Displays available commands
     */
    public static void displayCommands() {
        System.out.println("\nAVAILABLE COMMANDS:");
        System.out.println("-".repeat(40));
        System.out.println("move <from> <to>  - Make a move (e.g., 'move a1 b2')");
        System.out.println("help              - Show this help message");
        System.out.println("board             - Redisplay the board");
        System.out.println("info              - Show game information");
        System.out.println("moves             - Show recent moves");
        System.out.println("valid             - Show valid moves for current player");
        System.out.println("pieces            - Show movable pieces");
        System.out.println("history           - Show player game history");
        System.out.println("stats             - Show player statistics");
        System.out.println("forfeit           - Forfeit the game");
        System.out.println("quit              - Quit the game");
        System.out.println("-".repeat(40));
    }
    
    /**
     * Displays valid moves for current player
     */
    public static void displayValidMoves(GameEngine engine) {
        List<String> validMoves = engine.getValidMoves();
        
        if (validMoves.isEmpty()) {
            System.out.println("No valid moves available!");
            return;
        }
        
        System.out.println("\nVALID MOVES for " + engine.getCurrentPlayerName() + ":");
        System.out.println("-".repeat(40));
        
        for (int i = 0; i < validMoves.size(); i++) {
            System.out.printf("%-8s", validMoves.get(i));
            if ((i + 1) % 6 == 0) System.out.println();
        }
        if (validMoves.size() % 6 != 0) System.out.println();
        System.out.println("-".repeat(40));
    }
    
    /**
     * Displays movable pieces for current player
     */
    public static void displayMovablePieces(GameEngine engine) {
        List<Piece> pieces = engine.getMovablePieces();
        
        if (pieces.isEmpty()) {
            System.out.println("No movable pieces!");
            return;
        }
        
        System.out.println("\nMOVABLE PIECES for " + engine.getCurrentPlayerName() + ":");
        System.out.println("-".repeat(40));
        
        for (Piece piece : pieces) {
            System.out.printf("%s at %s\n", 
                piece.getType().getName(), 
                piece.getPosition().toStringNotation());
        }
        System.out.println("-".repeat(40));
    }
    
    /**
     * Displays game end message
     */
    public static void displayGameEnd(GameEngine engine) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("                GAME OVER");
        System.out.println("=".repeat(50));
        
        String winner = engine.getWinnerName();
        if (winner != null) {
            System.out.println("🎉 WINNER: " + winner + " 🎉");
        } else {
            System.out.println("Game ended.");
        }
        
        System.out.println("Total moves: " + engine.getMoveHistory().size());
        System.out.println("=".repeat(50));
    }
    
    /**
     * Displays welcome message
     */
    public static void displayWelcome() {
        System.out.println("=".repeat(60));
        System.out.println("           WELCOME TO XOU DOU QI (JUNGLE CHESS)");
        System.out.println("=".repeat(60));
        System.out.println("A traditional Chinese board game for two players.");
        System.out.println("Goal: Move any piece to the opponent's sanctuary or");
        System.out.println("      capture all opponent pieces to win!");
        System.out.println("=".repeat(60));
    }
    
    /**
     * Displays error message
     */
    public static void displayError(String message) {
        System.out.println("❌ ERROR: " + message);
    }
    
    /**
     * Displays success message
     */
    public static void displaySuccess(String message) {
        System.out.println("✅ " + message);
    }
    
    /**
     * Displays info message
     */
    public static void displayInfo(String message) {
        System.out.println("ℹ️  " + message);
    }
}
