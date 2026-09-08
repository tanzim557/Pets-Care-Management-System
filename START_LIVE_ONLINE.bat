@echo off
TITLE Pets Care Hospital - Live Online Tunnel
CLS

echo ========================================================
echo    PETS CARE MANAGEMENT SYSTEM - LIVE ONLINE LAUNCHER
echo ========================================================
echo.

:: 1. Check if application jar exists, build if not
if not exist "target\pets-care-system-1.0.0.jar" (
    echo [INFO] Building Spring Boot JAR...
    call mvn clean package -DskipTests
)

:: 2. Check if server already running on port 8082
set server_running=0
for /f "tokens=5" %%a in ('netstat -aon ^| find ":8082" ^| find "LISTENING"') do (
    set server_running=1
)

if "%server_running%"=="0" (
    echo [INFO] Starting Spring Boot Server on Port 8082...
    start "Pets Care Server" /B java -jar target\pets-care-system-1.0.0.jar --server.port=8082
    timeout /t 6 >nul
) else (
    echo [INFO] Spring Boot Server is already active on Port 8082.
)

:: 3. Check cloudflared.exe
if not exist "cloudflared.exe" (
    echo [ERROR] cloudflared.exe not found!
    pause
    exit /b 1
)

echo.
echo ========================================================
echo  Generating Live Public HTTPS URL via Cloudflare...
echo  (Copy the URL shown below and share it with anyone!)
echo ========================================================
echo.

cloudflared.exe tunnel --url http://localhost:8082
pause
