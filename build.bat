@echo off
echo Compiling Xou Dou Qi Java Project...

:: Create output directory
if not exist "build" mkdir build

:: Compile all Java files
javac -cp "lib\*" -d build java-src\com\xoudouqi\*.java java-src\com\xoudouqi\model\*.java java-src\com\xoudouqi\database\*.java

if %ERRORLEVEL% == 0 (
    echo Compilation successful!
    echo.
    echo To run the game:
    echo java -cp "build;lib\*" com.xoudouqi.XouDouQiGame
) else (
    echo Compilation failed!
)

pause
