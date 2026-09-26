# Script untuk menjalankan aplikasi Warmindo Digital Express
[Console]::OutputEncoding = [System.Text.Encoding]::UTF8

Write-Host "========================================================" -ForegroundColor Yellow
Write-Host "  Memulai Warmindo Digital Express (Java Swing GUI)    " -ForegroundColor Green
Write-Host "========================================================" -ForegroundColor Yellow

if (-not (Test-Path "bin")) {
    Write-Host "Mengompilasi source code..." -ForegroundColor Cyan
    javac -encoding UTF-8 -d bin src/model/*.java src/service/*.java src/gui/*.java src/Main.java
}

java "-Dfile.encoding=UTF-8" -cp bin Main
