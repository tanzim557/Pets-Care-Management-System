@echo off
echo Stopping MySQL service...
net stop MySQL80

echo Starting MySQL without grant tables...
start /B mysqld --skip-grant-tables --skip-networking --user=root

echo Waiting for MySQL to start...
timeout /t 5 /nobreak

echo Resetting root password...
mysql -u root --connect-expired-password < reset_password.sql

echo Stopping skip-grant-tables MySQL...
mysqladmin -u root shutdown

echo Starting MySQL normally...
net start MySQL80

echo Done! Password reset to: root1234
pause
