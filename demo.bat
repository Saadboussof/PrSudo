@echo off
echo =============================================
echo    XOU DOU QI DEMO - Quick Play Mode
echo =============================================
echo.
echo This demo will start the game in Quick Play mode
echo (no login required) so you can immediately test
echo the game functionality.
echo.
echo Game Controls:
echo - Use commands like: move a1 b2
echo - Type 'help' for all available commands
echo - Type 'valid' to see valid moves
echo - Type 'quit' to exit
echo.
echo Starting game...
echo.
pause

java -cp "build;lib\*" com.xoudouqi.XouDouQiGame

echo.
echo Demo completed!
pause
