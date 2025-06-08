package com.xoudouqi;

import com.xoudouqi.database.*;
import java.util.*;


public class XouDouQiGame {
    private Scanner scanner;
    private DatabaseManager dbManager;
    private PlayerAccount currentPlayer1;
    private PlayerAccount currentPlayer2;
    
    public XouDouQiGame() {
        this.scanner = new Scanner(System.in);
        this.dbManager = new DatabaseManager();
    }
    
    public static void main(String[] args) {
        XouDouQiGame game = new XouDouQiGame();
        game.run();
    }
    
    public void run() {
        ConsoleDisplay.displayWelcome();
        
        try {
            mainMenu();
        } catch (Exception e) {
            ConsoleDisplay.displayError("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            dbManager.close();
            scanner.close();
        }
    }
    
    private void mainMenu() {
        while (true) {
            System.out.println("\n" + "=".repeat(40));
            System.out.println("              MAIN MENU");
            System.out.println("=".repeat(40));
            System.out.println("1. Login and Play");
            System.out.println("2. Create New Account");
            System.out.println("3. View Player Rankings");
            System.out.println("4. Quick Play (No Login)");
            System.out.println("5. Exit");
            System.out.println("=".repeat(40));
            System.out.print("Choose an option (1-5): ");
            
            String choice = scanner.nextLine().trim();
            
            switch (choice) {
                case "1":
                    loginAndPlay();
                    break;
                case "2":
                    createAccount();
                    break;
                case "3":
                    viewRankings();
                    break;
                case "4":
                    quickPlay();
                    break;
                case "5":
                    System.out.println("Thanks for playing Xou Dou Qi!");
                    return;
                default:
                    ConsoleDisplay.displayError("Invalid choice. Please try again.");
            }
        }
    }
    
    private void loginAndPlay() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("PLAYER LOGIN");
        System.out.println("-".repeat(30));
        
        // Player 1 login
        System.out.println("Player 1 Login:");
        currentPlayer1 = authenticatePlayer();
        if (currentPlayer1 == null) {
            ConsoleDisplay.displayError("Login failed for Player 1.");
            return;
        }
        
        ConsoleDisplay.displaySuccess("Player 1 logged in: " + currentPlayer1.getUsername());
        
        // Player 2 login
        System.out.println("\nPlayer 2 Login:");
        currentPlayer2 = authenticatePlayer();
        if (currentPlayer2 == null) {
            ConsoleDisplay.displayError("Login failed for Player 2.");
            return;
        }
        
        if (currentPlayer1.getUsername().equals(currentPlayer2.getUsername())) {
            ConsoleDisplay.displayError("Both players cannot use the same account.");
            return;
        }
        
        ConsoleDisplay.displaySuccess("Player 2 logged in: " + currentPlayer2.getUsername());
        
        // Start game
        startGame();
    }
    
    private PlayerAccount authenticatePlayer() {
        int attempts = 0;
        while (attempts < 3) {
            System.out.print("Username: ");
            String username = scanner.nextLine().trim();
            
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();
            
            PlayerAccount player = dbManager.authenticatePlayer(username, password);
            if (player != null) {
                return player;
            }
            
            attempts++;
            ConsoleDisplay.displayError("Invalid credentials. " + (3 - attempts) + " attempts remaining.");
        }
        return null;
    }
    
    private void createAccount() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("CREATE NEW ACCOUNT");
        System.out.println("-".repeat(30));
        
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty() || username.length() < 3) {
            ConsoleDisplay.displayError("Username must be at least 3 characters long.");
            return;
        }
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();
        
        if (password.isEmpty() || password.length() < 4) {
            ConsoleDisplay.displayError("Password must be at least 4 characters long.");
            return;
        }
        
        if (dbManager.createPlayer(username, password)) {
            ConsoleDisplay.displaySuccess("Account created successfully for " + username + "!");
        } else {
            ConsoleDisplay.displayError("Failed to create account. Username might already exist.");
        }
    }
    
    private void viewRankings() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    PLAYER RANKINGS");
        System.out.println("=".repeat(60));
        
        List<PlayerAccount> players = dbManager.getAllPlayers();
        
        if (players.isEmpty()) {
            System.out.println("No players found.");
            return;
        }
        
        System.out.printf("%-4s %-15s %-8s %-6s %-6s %-8s\n", 
                "Rank", "Username", "Games", "Won", "Lost", "Win Rate");
        System.out.println("-".repeat(60));
        
        for (int i = 0; i < players.size(); i++) {
            PlayerAccount player = players.get(i);
            System.out.printf("%-4d %-15s %-8d %-6d %-6d %-8.1f%%\n",
                    i + 1,
                    player.getUsername(),
                    player.getGamesPlayed(),
                    player.getGamesWon(),
                    player.getGamesLost(),
                    player.getWinRate());
        }
        System.out.println("=".repeat(60));
    }
    
    private void quickPlay() {
        System.out.println("\n" + "-".repeat(30));
        System.out.println("QUICK PLAY MODE");
        System.out.println("-".repeat(30));
        System.out.println("Starting game without player accounts...");
        
        currentPlayer1 = null;
        currentPlayer2 = null;
        
        startGame();
    }
    
    private void startGame() {
        GameEngine engine = new GameEngine(dbManager);
        if (currentPlayer1 != null && currentPlayer2 != null) {
            engine.setPlayers(currentPlayer1, currentPlayer2);
        }
        
        ConsoleDisplay.displayBoard(engine.getBoard());
        ConsoleDisplay.displayGameInfo(engine);
        ConsoleDisplay.displayCommands();
        
        // Game loop
        while (!engine.isGameEnded()) {
            System.out.println("\n" + engine.getCurrentPlayerName() + "'s turn.");
            System.out.print("Enter command: ");
            
            String input = scanner.nextLine().trim().toLowerCase();
            
            if (input.isEmpty()) {
                continue;
            }
            
            if (handleCommand(input, engine)) {
                // Command was handled, continue
            } else {
                ConsoleDisplay.displayError("Invalid command. Type 'help' for available commands.");
            }
        }
        
        ConsoleDisplay.displayGameEnd(engine);
        
        // Show final board
        ConsoleDisplay.displayBoard(engine.getBoard());
        
        // Show game summary
        if (currentPlayer1 != null && currentPlayer2 != null) {
            showGameHistory();
        }
    }
    
    private boolean handleCommand(String input, GameEngine engine) {
        String[] parts = input.split("\\s+");
        String command = parts[0];
        
        switch (command) {
            case "move":
                if (parts.length == 3) {
                    String moveStr = parts[1] + " " + parts[2];
                    if (engine.makeMove(moveStr)) {
                        ConsoleDisplay.displaySuccess("Move executed: " + moveStr);
                        ConsoleDisplay.displayBoard(engine.getBoard());
                        ConsoleDisplay.displayGameInfo(engine);
                    } else {
                        ConsoleDisplay.displayError("Invalid move: " + moveStr);
                    }
                } else {
                    ConsoleDisplay.displayError("Usage: move <from> <to> (e.g., move a1 b2)");
                }
                return true;
                
            case "help":
                ConsoleDisplay.displayCommands();
                return true;
                
            case "board":
                ConsoleDisplay.displayBoard(engine.getBoard());
                return true;
                
            case "info":
                ConsoleDisplay.displayGameInfo(engine);
                return true;
                
            case "moves":
                ConsoleDisplay.displayRecentMoves(engine, 10);
                return true;
                
            case "valid":
                ConsoleDisplay.displayValidMoves(engine);
                return true;
                
            case "pieces":
                ConsoleDisplay.displayMovablePieces(engine);
                return true;
                
            case "history":
                if (currentPlayer1 != null && currentPlayer2 != null) {
                    showPlayerHistory(engine.getCurrentPlayerAccount());
                } else {
                    ConsoleDisplay.displayInfo("History not available in quick play mode.");
                }
                return true;
                
            case "stats":
                if (currentPlayer1 != null && currentPlayer2 != null) {
                    showPlayerStats();
                } else {
                    ConsoleDisplay.displayInfo("Stats not available in quick play mode.");
                }
                return true;
                
            case "forfeit":
                System.out.print("Are you sure you want to forfeit? (yes/no): ");
                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("yes") || confirm.equals("y")) {
                    engine.forfeit();
                    ConsoleDisplay.displayInfo(engine.getCurrentPlayerName() + " forfeited the game.");
                }
                return true;
                
            case "quit":
                System.out.print("Are you sure you want to quit the game? (yes/no): ");
                String quitConfirm = scanner.nextLine().trim().toLowerCase();
                if (quitConfirm.equals("yes") || quitConfirm.equals("y")) {
                    if (!engine.isGameEnded()) {
                        engine.forfeit();
                    }
                    return true;
                }
                return true;
                
            default:
                return false;
        }
    }
    
    private void showPlayerHistory(PlayerAccount player) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("GAME HISTORY for " + player.getUsername());
        System.out.println("=".repeat(50));
        
        List<GameHistory> games = dbManager.getPlayerGameHistory(player.getUsername(), 10);
        
        if (games.isEmpty()) {
            System.out.println("No game history found.");
            return;
        }
        
        for (GameHistory game : games) {
            System.out.println(game.toString());
        }
        System.out.println("=".repeat(50));
    }
    
    private void showPlayerStats() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("PLAYER STATISTICS");
        System.out.println("=".repeat(50));
        System.out.println("Player 1: " + currentPlayer1.toString());
        System.out.println("Player 2: " + currentPlayer2.toString());
        System.out.println("=".repeat(50));
    }
    
    private void showGameHistory() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("POST-GAME STATISTICS");
        System.out.println("=".repeat(50));
        
        // Refresh player stats from database
        PlayerAccount p1Updated = dbManager.authenticatePlayer(
                currentPlayer1.getUsername(), currentPlayer1.getPassword());
        PlayerAccount p2Updated = dbManager.authenticatePlayer(
                currentPlayer2.getUsername(), currentPlayer2.getPassword());
        
        if (p1Updated != null && p2Updated != null) {
            System.out.println("Updated Stats:");
            System.out.println("Player 1: " + p1Updated.toString());
            System.out.println("Player 2: " + p2Updated.toString());
        }
        
        System.out.println("=".repeat(50));
    }
}
