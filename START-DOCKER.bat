@echo off
TITLE Pets Care System - Professional Setup
CLS

ECHO ========================================================
ECHO    PETS CARE MANAGEMENT SYSTEM - DOCKER SETUP (Win)
ECHO ========================================================
ECHO.

:: --- STEP 1: PRE-FLIGHT CHECKS ---
ECHO [1/4] Checking System Requirements...

:: Check Docker Installation
docker --version >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Docker is NOT installed.
    ECHO Please install Docker Desktop: https://www.docker.com/products/docker-desktop
    PAUSE
    EXIT /B 1
)

:: Check Docker Running State
docker info >nul 2>&1
IF %ERRORLEVEL% NEQ 0 (
    ECHO [ERROR] Docker is not running!
    ECHO Please START Docker Desktop and wait for the whale icon to stop animating.
    PAUSE
    EXIT /B 1
)

:: --- STEP 2: FILE INTEGRITY CHECKS ---
ECHO [2/4] Verifying Project Files...

IF NOT EXIST "Dockerfile" (
    ECHO [ERROR] Critical file missing: Dockerfile
    PAUSE
    EXIT /B 1
)
IF NOT EXIST "docker-compose.yml" (
    ECHO [ERROR] Critical file missing: docker-compose.yml
    PAUSE
    EXIT /B 1
)
IF NOT EXIST "db_init.sql" (
    ECHO [WARNING] db_init.sql not found. Database will start EMPTY.
)

:: --- STEP 3: EXECUTION ---
ECHO [3/4] Launching System...
ECHO.
ECHO [INFO] Docker will now download required ingredients (MySQL, Java).
ECHO [INFO] If this is the FIRST run, it may take 2-5 minutes.
ECHO [INFO] Subsequent runs will be instant.
ECHO.

docker-compose up -d --build

IF %ERRORLEVEL% NEQ 0 (
    ECHO.
    ECHO [ERROR] Something went wrong during startup.
    ECHO 1. Make sure no other server is running on port 8080.
    ECHO 2. Try restarting Docker Desktop.
    PAUSE
    EXIT /B 1
)

:: --- STEP 4: SUCCESS ---
ECHO.
ECHO [4/4] System is Online!
ECHO [SUCCESS] Database is connected.
ECHO [SUCCESS] Application is running.
ECHO.
ECHO Opening Browser in 5 seconds...
TIMEOUT /T 5 >nul
START http://localhost:8080

ECHO.
ECHO ========================================================
ECHO    SERVER RUNNING IN BACKGROUND
ECHO    You can close this window.
ECHO ========================================================
PAUSE >nul
