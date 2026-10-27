$ErrorActionPreference = 'Stop'
$serverDir = $PSScriptRoot
$modsDir = Join-Path $serverDir 'mods'
$projectDir = Split-Path -Parent $serverDir
$modSource = Join-Path $projectDir 'build\libs\player-cart-boost-1.0.0.jar'
$modTarget = Join-Path $modsDir 'player-cart-boost-1.0.0.jar'
$launcherName = 'fabric-server-mc.26.3-loader.0.19.5-launcher.1.1.2.jar'
$launcherPath = Join-Path $serverDir $launcherName
$apiName = 'fabric-api-0.165.0+26.3.jar'
$apiPath = Join-Path $modsDir $apiName

if (-not (Test-Path -LiteralPath $modSource)) {
    throw "Built mod not found: $modSource`nBuild the project first with ..\gradlew.bat build."
}
New-Item -ItemType Directory -Force -Path $modsDir | Out-Null
Copy-Item -LiteralPath $modSource -Destination $modTarget -Force

if (-not (Test-Path -LiteralPath $launcherPath)) {
    Write-Host 'Downloading Fabric server launcher for Minecraft 26.3 / Loader 0.19.5...'
    Invoke-WebRequest -Uri 'https://meta.fabricmc.net/v2/versions/loader/26.3/0.19.5/1.1.2/server/jar' -OutFile $launcherPath
}
if (-not (Test-Path -LiteralPath $apiPath)) {
    Write-Host 'Downloading Fabric API 0.165.0+26.3...'
    Invoke-WebRequest -Uri 'https://maven.fabricmc.net/net/fabricmc/fabric-api/fabric-api/0.165.0%2B26.3/fabric-api-0.165.0%2B26.3.jar' -OutFile $apiPath
}
$eulaPath = Join-Path $serverDir 'eula.txt'
Set-Content -LiteralPath $eulaPath -Value 'eula=true' -Encoding ascii
Write-Host ''
Write-Host 'Setup complete. EULA acceptance is configured in eula.txt.'
