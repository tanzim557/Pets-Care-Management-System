# 🩺 Pets Care Management System - Portable Edition

## 📦 What's This Package?

This is a **ready-to-deploy, portable version** of the Pets Care Management System that runs on **any computer** (Windows or Mac) with just **one click**.

### ✨ New in This Version (Dec 10, 2025):
- 💰 **Donation System** with secure payment gateway
- 📊 **Enhanced Admin Dashboard** (Doctors, Rescue Teams, Recovered Pets stats)
- 🔍 **Fixed Application Tracking** with real-time status updates
- 🙏 **Custom Thank You Pages** for donations and orders
- 🐛 **All Bug Fixes Applied**

---

## 🎯 Quick Start (Choose Your Platform)

### For Windows Users:
1. Extract this folder anywhere
2. Double-click **`START-DOCKER.bat`**
3. Wait 2-5 minutes (first time only)
4. Browser opens automatically → `http://localhost:8080`

### For Mac/Linux Users:
1. Extract this folder anywhere
2. Open Terminal and navigate to this folder
3. Run: `chmod +x start-docker.sh` (first time only)
4. Run: `./start-docker.sh`
5. Browser opens automatically → `http://localhost:8080`

---

## 📋 Requirements

- **Docker Desktop** (Download: https://www.docker.com/products/docker-desktop)
- **5GB free disk space**
- **2GB RAM** minimum

That's it! No Java, MySQL, or other installations needed.

---

## 🔐 Default Login Credentials

### Admin Account:
- **Username:** `admin`
- **Password:** `admin123`

### Doctor Account:
- **Username:** `rupa`
- **Password:** `1234`

### Rescue Account:
- **Username:** `rescue1`
- **Password:** `1234`

---

## 📁 Important Files

- `START-DOCKER.bat` - Windows startup script
- `start-docker.sh` - Mac/Linux startup script  
- `docker-compose.yml` - Configuration file
- `db_init.sql` - Your database with all data

**⚠️ DO NOT DELETE `db_init.sql`** - This contains your database!

---

## 🛑 Stopping the System

### Windows:
```cmd
docker-compose down
```

### Mac/Linux:
```bash
docker-compose down
```

Or just close Docker Desktop (system stops automatically).

---

## 💡 What If Something Goes Wrong?

### "Port 8080 already in use"
**Solution:** Close any other application using port 8080, or edit `docker-compose.yml` and change line 50 to use port 8081.

### "Docker not running"
**Solution:** Open Docker Desktop and wait for it to start completely.

### "Can't connect to database"
**Solution:** Run these commands:
```bash
docker-compose down
docker-compose up -d --build
```

---

## 📱 Features Included

✅ Emergency Rescue Management  
✅ Doctor & Rescue Team Dashboards  
✅ Shop with Payment Integration  
✅ Pet Adoption System  
✅ SMS Notifications (auto-configured)  
✅ Real-time Application Tracking  
✅ Donation System with Payment  
✅ Admin Analytics Dashboard

---

## 🔄 Moving to Another Computer

1. **Copy this entire folder** to the new computer
2. Install Docker Desktop on the new computer
3. Run the startup script
4. **Done!** Your data transfers automatically.

---

## 🌐 Access Points

After starting:
- **Main Site:** http://localhost:8080
- **Admin Panel:** http://localhost:8080/admin.html
- **Doctor Dashboard:** http://localhost:8080/doctor.html
- **Rescue Dashboard:** http://localhost:8080/rescue.html
- **Track Application:** http://localhost:8080/track.html

---

## 📊 Database Access (Optional)

Want to view the database directly?

**Using MySQL Workbench or similar:**
- Host: `localhost`
- Port: `3307`  
- Database: `PetsCare`
- Username: `petscare_user`
- Password: `petscare_pass`

---

## 🆘 Emergency Commands

**View application logs:**
```bash
docker-compose logs -f app
```

**Restart everything:**
```bash
docker-compose restart
```

**Fresh start (keeps data):**
```bash
docker-compose down
docker-compose up -d --build
```

**Complete reset (deletes ALL data):**
```bash
docker-compose down -v
```

---

## 📞 Support Contact

For technical support, check:
1. Application logs: `docker-compose logs app`
2. Full documentation: `DOCKER-README.md`

---

**Version:** 2.0 Portable Edition  
**Last Updated:** December 10, 2025  
**Compatibility:** Windows 10+, macOS 10.15+, Linux (Ubuntu 20.04+)
