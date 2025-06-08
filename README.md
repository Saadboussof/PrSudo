# Xou Dou Qi (Jungle Chess) - Java Console Game

## Description
A Java console application implementing the traditional Chinese board game Xou Dou Qi (Animal Chess/Jungle Chess) for two players on the same machine.

## Features
- Player account management with embedded database (H2)
- Complete game logic implementation
- Simple console interface with text commands
- Automatic turn management
- Game history tracking
- Win/loss statistics display

## Game Rules
The game is played on a 9x7 board with special zones:
- Rivers (blue areas) that only certain animals can cross
- Traps (green areas) that weaken enemy pieces
- Sanctuaries (orange areas) where pieces cannot be captured

### Animal Hierarchy
1. ELEPHANT (strongest)
2. LION
3. TIGER
4. PANTHER
5. CHIEN (Dog)
6. LOUP (Wolf)
7. CHAT (Cat)
8. RAT (weakest, but can defeat elephant)

## How to Run

### Quick Start
1. Run `build.bat` to compile the project
2. Run `run.bat` to start the game
3. Or run `demo.bat` for a quick demo

### Manual Compilation and Execution
```bash
# Compile
javac -cp "lib\*" -d build src\main\java\com\xoudouqi\*.java src\main\java\com\xoudouqi\model\*.java src\main\java\com\xoudouqi\database\*.java

# Run
java -cp "build;lib\*" com.xoudouqi.XouDouQiGame
```

### Game Options
- **Login and Play**: Create accounts and track statistics
- **Quick Play**: Jump straight into a game without accounts
- **View Rankings**: See player leaderboards
- **Create Account**: Register new players

## Technologies Used
- Java 8+
- H2 Database (embedded)
- Console I/O

## Project Status
✅ **COMPLETED** - All features from the specification have been implemented and tested:

- ✅ Player account management with H2 database
- ✅ Complete Xou Dou Qi game logic with all rules
- ✅ Console interface with text commands
- ✅ Automatic turn management
- ✅ Game history and statistics tracking
- ✅ Player rankings and match history
- ✅ Win condition detection
- ✅ Move validation and piece capture logic
- ✅ Special terrain interactions (water, traps, sanctuaries)
- ✅ Animal hierarchy system with special rules

The project is ready to run and fully functional!
