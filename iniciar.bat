@echo off
chcp 65001 >nul
echo Compilando o Sudoku...
javac -encoding UTF-8 --module-path "lib" --add-modules javafx.controls -d bin src\TabuleiroSudoku.java src\TelaSudoku.java
if %errorlevel% neq 0 (
    echo Erro ao compilar!
    pause
    exit /b %errorlevel%
)
echo Iniciando o jogo...
java --module-path "lib" --add-modules javafx.controls -cp bin TelaSudoku
