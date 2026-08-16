@echo off
chcp 65001 >nul 2>&1
setlocal enabledelayedexpansion
title FRP Client Launcher
cd /d "%~dp0"
cls

echo.
echo   ============================================================
echo             FRP Tunnel Client
echo   ============================================================
echo.
echo     Server:   39.102.79.179:443
echo     Local:    127.0.0.1:8080  ^(Spring Boot^)
echo     Public:   39.102.79.179:28080
echo.
echo     Access:   http://39.102.79.179:28080
echo     WeChat:   http://39.102.79.179/wx/path
echo.
echo   ============================================================
echo.

:: ---- Pre-flight checks ----
if not exist "frpc.exe" (
    echo   [ERROR] frpc.exe not found in current directory^^!
    echo.
    goto :die
)
if not exist "frpc.toml" (
    echo   [ERROR] frpc.toml not found in current directory^^!
    echo.
    goto :die
)

:: ---- Server reachability ----
echo   [..] Checking server 39.102.79.179 ...
ping -n 1 -w 3000 39.102.79.179 >nul 2>&1
if !errorlevel! equ 0 (
    echo   [OK] Server reachable
) else (
    echo   [!!] Server unreachable - will attempt connection anyway
)
echo.

:menu
echo   ---- Menu ----
echo.
echo     1. Start tunnel
echo     2. Retry connection
echo     3. Check server status
echo     4. Help
echo     0. Exit
echo.
set "opt="
set /p "opt=   Choose [0-4]: "

if "!opt!"=="1" goto :start
if "!opt!"=="2" goto :start
if "!opt!"=="3" goto :diag
if "!opt!"=="4" goto :help
if "!opt!"=="0" exit /b

echo.
echo   Invalid option, try again.
echo.
goto :menu

:start
echo.
echo   [*] Connecting to 39.102.79.179:443 ...
echo   [i] Press Ctrl+C to stop
echo   -----------------------------------------------------------
echo.
frpc.exe -c frpc.toml
set "ec=!errorlevel!"
echo.
echo   -----------------------------------------------------------
if !ec! equ 0 (
    echo   [OK] Connection closed normally.
) else (
    echo   [FAIL] Exit code !ec! - server unreachable or config error.
    echo.
    echo   Common fixes:
    echo     - Verify server 39.102.79.179 is running frps
    echo     - Check firewall allows port 443
    echo     - Confirm local Spring Boot is running on port 8080
)
echo.
echo   Back to menu in 5s, or press any key ...
timeout /t 5 /nobreak >nul
echo.
goto :menu

:diag
echo.
echo   ---- Diagnostics ----
echo.
echo   Time:    !date! !time!
echo   Config:  frpc.toml
echo.
echo   [1/3] Pinging server ...
ping -n 3 -w 3000 39.102.79.179
echo.
echo   [2/3] Testing port 443 ...
powershell -Command "$t = New-Object Net.Sockets.TcpClient; try { $t.Connect('39.102.79.179', 443); Write-Host '   Port 443: OPEN'; $t.Close() } catch { Write-Host '   Port 443: CLOSED or filtered' }" 2>nul
echo.
echo   [3/3] Testing port 28080 ...
powershell -Command "$t = New-Object Net.Sockets.TcpClient; try { $t.Connect('39.102.79.179', 28080); Write-Host '   Port 28080: OPEN'; $t.Close() } catch { Write-Host '   Port 28080: CLOSED or filtered' }" 2>nul
echo.
echo   ---- End diagnostics ----
echo.
pause
goto :menu

:help
echo.
echo   ============================================================
echo                  FRP Client Help
echo   ============================================================
echo.
echo   What it does:
echo     Exposes your local Spring Boot (port 8080) to the
echo     internet via port 28080 on 39.102.79.179.
echo.
echo   Two access methods:
echo.
echo     1. Direct (with port):
echo        http://39.102.79.179:28080/your-api-path
echo.
echo     2. Via Nginx reverse proxy (port 80, for WeChat):
echo        http://39.102.79.179/wx/your-api-path
echo        The /wx/ prefix is stripped by Nginx automatically.
echo.
echo   WeChat callback URL example:
echo     http://39.102.79.179/wx/callback
echo.
echo   Prerequisites:
echo     - Spring Boot running locally on port 8080
echo     - Server 39.102.79.179 running frps
echo.
echo   Config file: frpc.toml
echo.
echo   ============================================================
echo.
pause
goto :menu

:die
echo   Press any key to exit ...
pause >nul
exit /b 1
