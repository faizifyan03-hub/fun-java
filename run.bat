@echo off
chcp 65001 > nul
echo ========================================================
echo   Memulai Warmindo Digital Express (Java Swing GUI)
echo ========================================================

if not exist bin (
    echo Mengompilasi source code...
    javac -encoding UTF-8 -d bin src\model\*.java src\service\*.java src\gui\*.java src\Main.java
)

java "-Dfile.encoding=UTF-8" -cp bin Main
pause
