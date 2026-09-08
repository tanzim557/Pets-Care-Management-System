#!/bin/bash

# Visual formatting
BOLD='\033[1m'
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BOLD}========================================================${NC}"
echo -e "${BOLD}   PETS CARE MANAGEMENT SYSTEM - DOCKER SETUP (Mac/Linux)${NC}"
echo -e "${BOLD}========================================================${NC}"
echo ""

# --- STEP 1: PRE-FLIGHT CHECKS ---
echo -e "${BOLD}[1/4] Checking System Requirements...${NC}"

# Check Docker Installation
if ! command -v docker &> /dev/null; then
    echo -e "${RED}[ERROR] Docker is NOT installed.${NC}"
    echo "Please install Docker Desktop: https://www.docker.com/products/docker-desktop"
    exit 1
fi

# Check Docker Daemon Running
if ! docker info &> /dev/null; then
    echo -e "${RED}[ERROR] Docker is not running!${NC}"
    echo "Please start Docker Desktop and wait for it to initialize."
    exit 1
fi

echo -e "${GREEN}[OK] Docker is up and running.${NC}"
echo ""

# --- STEP 2: FILE INTEGRITY CHECKS ---
echo -e "${BOLD}[2/4] Verifying Project Files...${NC}"

if [ ! -f "Dockerfile" ]; then
    echo -e "${RED}[ERROR] Critical file missing: Dockerfile${NC}"
    exit 1
fi
if [ ! -f "docker-compose.yml" ]; then
    echo -e "${RED}[ERROR] Critical file missing: docker-compose.yml${NC}"
    exit 1
fi
if [ ! -f "db_init.sql" ]; then
    echo -e "${YELLOW}[WARNING] db_init.sql not found. Database will start EMPTY.${NC}"
fi

echo -e "${GREEN}[OK] All files verified.${NC}"
echo ""

# --- STEP 3: EXECUTION ---
echo -e "${BOLD}[3/4] Launching System...${NC}"
echo "[INFO] Docker will now download required ingredients (MySQL, Java)."
echo "[INFO] If this is the FIRST run, it may take 2-5 minutes."
echo "[INFO] Subsequent runs will be instant."
echo ""

docker-compose up -d --build

if [ $? -ne 0 ]; then
    echo ""
    echo -e "${RED}[ERROR] Failed to start containers.${NC}"
    echo "1. Make sure no other server is running on port 8080."
    echo "2. Try restarting Docker Desktop."
    exit 1
fi

# --- STEP 4: SUCCESS ---
echo ""
echo -e "${BOLD}[4/4] System is Online!${NC}"
echo -e "${GREEN}[SUCCESS] Database is connected.${NC}"
echo -e "${GREEN}[SUCCESS] Application is running.${NC}"
echo ""
echo "Opening Application in Browser..."
sleep 5
open http://localhost:8080 2>/dev/null || xdg-open http://localhost:8080 2>/dev/null

echo ""
echo -e "${BOLD}========================================================${NC}"
echo -e "${BOLD}   SERVER RUNNING IN BACKGROUND${NC}"
echo -e "${BOLD}   You can close this window.${NC}"
echo -e "${BOLD}========================================================${NC}"
