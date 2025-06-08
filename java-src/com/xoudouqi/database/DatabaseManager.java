package com.xoudouqi.database;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Database manager for handling player accounts and game history
 * Uses H2 embedded database
 */
public class DatabaseManager {
    private static final String DB_URL = "jdbc:h2:./data/xoudouqi;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    private Connection connection;
    
    public DatabaseManager() {
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            initializeTables();
        } catch (Exception e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void initializeTables() throws SQLException {
        // Create players table
        String createPlayersTable = """
            CREATE TABLE IF NOT EXISTS players (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                username VARCHAR(50) UNIQUE NOT NULL,
                password VARCHAR(100) NOT NULL,
                games_played INTEGER DEFAULT 0,
                games_won INTEGER DEFAULT 0,
                games_lost INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                last_login TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;
        
        // Create game history table
        String createGameHistoryTable = """
            CREATE TABLE IF NOT EXISTS game_history (
                id INTEGER PRIMARY KEY AUTO_INCREMENT,
                player1_username VARCHAR(50) NOT NULL,
                player2_username VARCHAR(50) NOT NULL,
                winner VARCHAR(50),
                total_moves INTEGER DEFAULT 0,
                game_start_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                game_end_time TIMESTAMP,
                game_result VARCHAR(20)
            )
            """;
        
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayersTable);
            stmt.execute(createGameHistoryTable);
        }
    }
    
    // Player account methods
    public boolean createPlayer(String username, String password) {
        String sql = "INSERT INTO players (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Error creating player: " + e.getMessage());
            return false;
        }
    }
    
    public PlayerAccount authenticatePlayer(String username, String password) {
        String sql = "SELECT * FROM players WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                PlayerAccount player = new PlayerAccount();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setPassword(rs.getString("password"));
                player.setGamesPlayed(rs.getInt("games_played"));
                player.setGamesWon(rs.getInt("games_won"));
                player.setGamesLost(rs.getInt("games_lost"));
                player.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                player.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
                
                // Update last login
                updateLastLogin(username);
                
                return player;
            }
        } catch (SQLException e) {
            System.err.println("Error authenticating player: " + e.getMessage());
        }
        return null;
    }
    
    private void updateLastLogin(String username) {
        String sql = "UPDATE players SET last_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating last login: " + e.getMessage());
        }
    }
    
    public void updatePlayerStats(String username, boolean won) {
        String sql = won ? 
            "UPDATE players SET games_played = games_played + 1, games_won = games_won + 1 WHERE username = ?" :
            "UPDATE players SET games_played = games_played + 1, games_lost = games_lost + 1 WHERE username = ?";
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating player stats: " + e.getMessage());
        }
    }
    
    public List<PlayerAccount> getAllPlayers() {
        List<PlayerAccount> players = new ArrayList<>();
        String sql = "SELECT * FROM players ORDER BY games_won DESC, games_played DESC";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                PlayerAccount player = new PlayerAccount();
                player.setId(rs.getInt("id"));
                player.setUsername(rs.getString("username"));
                player.setGamesPlayed(rs.getInt("games_played"));
                player.setGamesWon(rs.getInt("games_won"));
                player.setGamesLost(rs.getInt("games_lost"));
                player.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                player.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
                players.add(player);
            }
        } catch (SQLException e) {
            System.err.println("Error getting all players: " + e.getMessage());
        }
        return players;
    }
    
    // Game history methods
    public int createGameHistory(String player1Username, String player2Username) {
        String sql = "INSERT INTO game_history (player1_username, player2_username) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, player1Username);
            pstmt.setString(2, player2Username);
            pstmt.executeUpdate();
            
            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("Error creating game history: " + e.getMessage());
        }
        return -1;
    }
    
    public void endGame(int gameId, String winnerUsername, String result, int totalMoves) {
        String sql = "UPDATE game_history SET winner = ?, game_result = ?, total_moves = ?, game_end_time = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, winnerUsername);
            pstmt.setString(2, result);
            pstmt.setInt(3, totalMoves);
            pstmt.setInt(4, gameId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error ending game: " + e.getMessage());
        }
    }
    
    public List<GameHistory> getPlayerGameHistory(String username, int limit) {
        List<GameHistory> games = new ArrayList<>();
        String sql = """
            SELECT * FROM game_history 
            WHERE player1_username = ? OR player2_username = ? 
            ORDER BY game_start_time DESC 
            LIMIT ?
            """;
        
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, username);
            pstmt.setInt(3, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                GameHistory game = new GameHistory();
                game.setId(rs.getInt("id"));
                game.setPlayer1Username(rs.getString("player1_username"));
                game.setPlayer2Username(rs.getString("player2_username"));
                game.setWinner(rs.getString("winner"));
                game.setTotalMoves(rs.getInt("total_moves"));
                game.setGameStartTime(rs.getTimestamp("game_start_time").toLocalDateTime());
                if (rs.getTimestamp("game_end_time") != null) {
                    game.setGameEndTime(rs.getTimestamp("game_end_time").toLocalDateTime());
                }
                game.setGameResult(rs.getString("game_result"));
                games.add(game);
            }
        } catch (SQLException e) {
            System.err.println("Error getting player game history: " + e.getMessage());
        }
        return games;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}
