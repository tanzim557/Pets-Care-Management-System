@echo off
echo === MySQL Password Reset Script ===
echo Stopping MySQL service...
net stop MySQL80
timeout /t 3 /nobreak >nul

echo Starting MySQL with skip-grant-tables...
start "" "C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqld.exe" --defaults-file="C:\ProgramData\MySQL\MySQL Server 8.0\my.ini" --skip-grant-tables --skip-networking

echo Waiting 5 seconds for MySQL to start...
timeout /t 5 /nobreak >nul

echo Resetting password...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysql.exe" -u root -e "FLUSH PRIVILEGES; ALTER USER 'root'@'localhost' IDENTIFIED BY 'root1234'; FLUSH PRIVILEGES;"

echo Stopping temporary MySQL instance...
"C:\Program Files\MySQL\MySQL Server 8.0\bin\mysqladmin.exe" -u root shutdown

timeout /t 3 /nobreak >nul

echo Starting MySQL normally...
net start MySQL80

echo === Password reset complete! New password: root1234 ===
pause
