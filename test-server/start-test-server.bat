@echo off
setlocal
cd /d "%~dp0"
set "SERVER_JAR=fabric-server-mc.26.3-loader.0.19.5-launcher.1.1.2.jar"
if not exist "%SERVER_JAR%" (
  echo Server launcher missing. Run setup-test-server.bat first.
  pause
  exit /b 1
)
if not exist "mods\player-cart-boost-1.0.0.jar" (
  echo Mod jar missing. Run setup-test-server.bat first.
  pause
  exit /b 1
)
findstr /I /C:"eula=true" eula.txt >nul 2>nul
if errorlevel 1 (
  echo Read the EULA at https://www.minecraft.net/eula and set eula=true in eula.txt if you agree.
  pause
  exit /b 1
)
java -Xms1G -Xmx2G -jar "%SERVER_JAR%" nogui
pause
