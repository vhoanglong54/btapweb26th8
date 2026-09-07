[CmdletBinding()]
param(
    [switch]$OpenBrowser,
    [switch]$SkipBuild
)

$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot

function Resolve-RuntimeDirectory([string]$variableName, [string]$fallbackPath) {
    $configuredPath = [Environment]::GetEnvironmentVariable($variableName, 'Process')
    $candidatePaths = @($configuredPath, $fallbackPath) | Where-Object { -not [string]::IsNullOrWhiteSpace($_) } | Select-Object -Unique
    foreach ($candidatePath in $candidatePaths) {
        if (Test-Path -LiteralPath $candidatePath -PathType Container) {
            return (Resolve-Path -LiteralPath $candidatePath).Path
        }
    }
    throw "$variableName is invalid. Set it or install the runtime at: $fallbackPath"
}

$javaHome = Resolve-RuntimeDirectory 'JAVA_HOME' 'D:\.tools_web\jdk-17.0.20.1+1'
$mavenHome = Resolve-RuntimeDirectory 'MAVEN_HOME' 'D:\.tools_web\apache-maven-3.9.11'
$tomcatHome = Resolve-RuntimeDirectory 'TOMCAT_HOME' 'D:\.tools_web\apache-tomcat-11.0.25'
$mavenCommand = Join-Path $mavenHome 'bin\mvn.cmd'
$startupCommand = Join-Path $tomcatHome 'bin\startup.bat'
$shutdownCommand = Join-Path $tomcatHome 'bin\shutdown.bat'
$serverConfig = Join-Path $tomcatHome 'conf\server.xml'
$pomFile = Join-Path $projectRoot 'pom.xml'
$warFile = Join-Path $projectRoot 'target\ServletCRUDMVC.war'
$deployedWar = Join-Path $tomcatHome 'webapps\ServletCRUDMVC.war'

if (-not (Test-Path -LiteralPath $mavenCommand -PathType Leaf)) { throw "Maven was not found: $mavenCommand" }
if (-not (Test-Path -LiteralPath $startupCommand -PathType Leaf)) { throw "Tomcat was not found: $startupCommand" }
if (-not (Test-Path -LiteralPath $shutdownCommand -PathType Leaf)) { throw "Tomcat shutdown command was not found: $shutdownCommand" }
if (-not (Test-Path -LiteralPath $pomFile -PathType Leaf)) { throw "pom.xml was not found: $pomFile" }

$env:JAVA_HOME = $javaHome
$env:JRE_HOME = $javaHome
$env:Path = "$javaHome\bin;$mavenHome\bin;" + $env:Path

if (-not $SkipBuild) {
    Write-Host 'Building WAR...'
    & $mavenCommand -f $pomFile clean package -DskipTests
    if ($LASTEXITCODE -ne 0) { throw "Build failed (exit code $LASTEXITCODE)." }
}

if (-not (Test-Path -LiteralPath $warFile -PathType Leaf)) { throw "WAR was not found: $warFile. Run again without -SkipBuild." }

$serverXmlContent = Get-Content -LiteralPath $serverConfig -Raw
$portMatch = [regex]::Match($serverXmlContent, '<Connector\s+port="(\d+)"\s+protocol="HTTP/1\.1"')
if (-not $portMatch.Success) { throw "Could not read the HTTP port from $serverConfig" }

$port = $portMatch.Groups[1].Value
$url = "http://localhost:$port/ServletCRUDMVC/login"

function Test-ListeningPort([int]$portNumber) {
    return $null -ne (Get-NetTCPConnection -State Listen -LocalPort $portNumber -ErrorAction SilentlyContinue | Select-Object -First 1)
}

if (Test-ListeningPort ([int]$port)) {
    Write-Host "Stopping Tomcat on port $port..."
    & $shutdownCommand | Out-Null
    for ($attempt = 1; $attempt -le 15; $attempt++) {
        if (-not (Test-ListeningPort ([int]$port))) { break }
        Start-Sleep -Seconds 1
    }
    if (Test-ListeningPort ([int]$port)) {
        $listener = Get-NetTCPConnection -State Listen -LocalPort ([int]$port) | Select-Object -First 1
        $listenerProcess = Get-CimInstance Win32_Process -Filter "ProcessId = $($listener.OwningProcess)"
        if ($listenerProcess.CommandLine -notlike "*$tomcatHome*") { throw "Port $port is used by another application. Stop it manually, then run again." }
        Stop-Process -Id $listener.OwningProcess -Force
        for ($attempt = 1; $attempt -le 10; $attempt++) {
            if (-not (Test-ListeningPort ([int]$port))) { break }
            Start-Sleep -Seconds 1
        }
    }
    if (Test-ListeningPort ([int]$port)) { throw "Tomcat could not be stopped on port $port." }
}

Copy-Item -LiteralPath $warFile -Destination $deployedWar -Force
Write-Host "Starting Tomcat on port $port..."
Start-Process -FilePath $startupCommand -WorkingDirectory (Join-Path $tomcatHome 'bin') -WindowStyle Hidden

for ($attempt = 1; $attempt -le 15; $attempt++) {
    try {
        $probe = Invoke-WebRequest -UseBasicParsing -Uri $url -TimeoutSec 3
        if ($probe.StatusCode -eq 200) {
            Write-Host "Web is ready: $url" -ForegroundColor Green
            if ($OpenBrowser) { Start-Process $url }
            exit 0
        }
    } catch {
        Start-Sleep -Seconds 2
    }
}

throw "Tomcat did not respond after 30 seconds. Check logs at $(Join-Path $tomcatHome 'logs')."
