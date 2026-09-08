# 🚀 Pets Care Management System - Docker Setup Guide

## ✨ Latest Updates (Dec 10, 2025)

### New Features Included in This Version:
- ✅ **Donation System** - Secure donation processing with SSLCommerz integration
- ✅ **Enhanced Admin Dashboard** - Shows Doctors, Rescue Teams, Recovered Pets, Total Donations
- ✅ **Fixed Track Status** - Real-time tracking with proper status synchronization
- ✅ **Dynamic Thank You Pages** - Different messages for donations vs orders
- ✅ **Complete Bug Fixes** - All reported issues resolved

---

## 📋 What's Included

This Docker setup provides:
- **MySQL 8.0 Database** (with automatic schema creation)
- **Spring Boot Application** (Java 17)
- **Automatic SMS Integration** (pre-configured)
- **One-Click Startup** (for both Windows & Mac)
- **Data Persistence** (your data is saved between restarts)

---

## 🛠️ Prerequisites

### For **Windows**:
1. Install **Docker Desktop for Windows**: https://www.docker.com/products/docker-desktop
2. Start Docker Desktop and wait for the whale icon to stabilize

### For **Mac/Linux**:
1. Install **Docker Desktop for Mac**: https://www.docker.com/products/docker-desktop
2. Start Docker Desktop and wait for it to be running

**To verify Docker is ready:**
```bash
docker --version
docker info
```

---

## 🚀 Quick Start

### **Windows Users:**
1. **Open File Explorer** and navigate to this folder
2. **Double-click** `START-DOCKER.bat`
3. Wait 2-5 minutes on first run (downloads MySQL & Java images)
4. Browser will open automatically at `http://localhost:8080`

### **Mac/Linux Users:**
1. **Open Terminal** and navigate to this folder:
   ```bash
   cd "/path/to/Pets-Care-Management-System-main"
   ```
2. Make the script executable (first time only):
   ```bash
   chmod +x start-docker.sh
   ```
3. **Run the startup script:**
   ```bash
   ./start-docker.sh
   ```
4. Wait 2-5 minutes on first run
5. Browser will open automatically at `http://localhost:8080`

---

## 📦 What Happens on First Run?

1. **Docker downloads:**
   - MySQL 8.0 image (~500MB)
   - Java 17 runtime image (~200MB)
   
2. **Application builds:**
   - Compiles Spring Boot app
   - Creates database tables automatically (including new Donation table)
   
3. **Services start:**
   - MySQL on port 3307 (mapped to internal 3306)
   - Application on port 8080

**Note:** Subsequent runs are instant (containers already exist)

---

## 🔧 Manual Docker Commands (Advanced)

If you prefer manual control:

### Start the system:
```bash
docker-compose up -d --build
```

### Stop the system:
```bash
docker-compose down
```

### View logs:
```bash
docker-compose logs -f app
```

### Restart just the app (if you change code):
```bash
docker-compose restart app
```

### Complete cleanup (removes data):
```bash
docker-compose down -v
```

---

## 🗄️ Database Configuration

**Connection Details (for external tools like MySQL Workbench):**
- **Host:** `localhost`
- **Port:** `3307`
- **Database:** `PetsCare`
- **Username:** `petscare_user`
- **Password:** `petscare_pass`

**Root Access:**
- **Username:** `root`
- **Password:** `rootpassword`

---

## 📱 SMS Configuration

SMS is pre-configured with your credentials:
- **API Key:** `RxE3j0OrkkWS8hYUPmRs`
- **Sender ID:** `8809617631158`

To change, edit `docker-compose.yml` lines 47-48.

---

## 🐛 Troubleshooting

### Problem: "Port 8080 already in use"
**Solution:** Stop any other applications using port 8080, or change the port in `docker-compose.yml` line 50 to `"8081:8080"`

### Problem: "Docker is not running"
**Solution:** Start Docker Desktop and wait for it to fully initialize (whale icon stops animating)

### Problem: "Cannot connect to database"
**Solution:** 
```bash
docker-compose down
docker-compose up -d
```

### Problem: "Lost my data after restart"
**Solution:** Don't use `docker-compose down -v` (the `-v` flag deletes data). Use `docker-compose down` instead.

### Problem: "Application won't start"
**Solution:**
1. Check logs: `docker-compose logs app`
2. Rebuild: `docker-compose up -d --build --force-recreate`

---

## 🔄 Updating the Application

When you make code changes:

1. **Stop the containers:**
   ```bash
   docker-compose down
   ```

2. **Rebuild and start:**
   ```bash
   docker-compose up -d --build
   ```

Your database data is preserved automatically!

---

## 📂 Project Structure

```
Pets-Care-Management-System-main/
├── Dockerfile                 # App build instructions
├── docker-compose.yml         # Orchestration config
├── START-DOCKER.bat          # Windows startup script
├── start-docker.sh           # Mac/Linux startup script
├── db_init.sql               # Initial database dump
├── pom.xml                   # Maven dependencies
└── src/                      # Application source code
```

---

## 🎯 Accessing the System

Once running, access:
- **Main Application:** http://localhost:8080
- **Admin Panel:** http://localhost:8080/admin.html
- **Doctor Panel:** http://localhost:8080/doctor.html
- **Rescue Panel:** http://localhost:8080/rescue.html

---

## 🛡️ Security Notes

**For Production Deployment:**
1. Change all passwords in `docker-compose.yml`
2. Use environment variables instead of hardcoded values
3. Enable SSL/HTTPS
4. Update SMS credentials
5. Use a production MySQL instance (not Docker)

---

## 📞 Support

For issues or questions:
1. Check logs: `docker-compose logs -f`
2. Restart services: `docker-compose restart`
3. Clean rebuild: `docker-compose down && docker-compose up -d --build`

---

## ✅ Verification Checklist

After starting, verify:
- [ ] Application loads at http://localhost:8080
- [ ] Admin Dashboard shows stats (Doctors, Rescuers, etc.)
- [ ] Donation button appears in navbar
- [ ] Track Status page works
- [ ] Shop page loads products

---

**Version:** 2.0 (Dec 2025)  
**Docker Compose Version:** 3.8  
**Java Version:** 17  
**MySQL Version:** 8.0
