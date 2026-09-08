@echo off
color 0B
echo ==============================================================
echo        Pets Care Management System - Server Toggle         
echo ==============================================================
echo.

set found=0
FOR /F "tokens=5" %%a IN ('netstat -aon ^| find ":8081" ^| find "LISTENING"') DO (
    set found=1
    set pid=%%a
)

if "%found%"=="1" goto stop_server
goto start_server

:stop_server
echo Server is currently RUNNING on Port 8081 (PID: %pid%).
echo.
echo Stopping the server...
taskkill /F /PID %pid%
echo.
echo Server successfully stopped!
echo ==============================================================
pause
exit

:start_server
echo Server is currently OFF.
echo.
echo Starting the Spring Boot server... Please wait a few seconds.
echo.
echo --------------------------------------------------------------
echo Website Link: http://localhost:8081
echo --------------------------------------------------------------
echo Note: You can Ctrl+Click the link above in some terminals, or 
echo simply copy-paste it into your browser.
echo.
echo To stop the server later, simply double-click this file again!
echo ==============================================================
echo.
call mvn clean spring-boot:run
echo.
echo Server process ended.
pause
