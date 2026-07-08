@echo off
setlocal

set MAVEN_VERSION=3.9.11
set MAVEN_BASE=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_HOME=%MAVEN_BASE%\apache-maven-%MAVEN_VERSION%
set MAVEN_ZIP=%MAVEN_BASE%\apache-maven-%MAVEN_VERSION%-bin.zip

if not exist "%MAVEN_HOME%\bin\mvn.cmd" (
  echo Downloading Apache Maven %MAVEN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference='Stop';" ^
    "New-Item -ItemType Directory -Force -Path '%MAVEN_BASE%' | Out-Null;" ^
    "$url='https://archive.apache.org/dist/maven/maven-3/%MAVEN_VERSION%/binaries/apache-maven-%MAVEN_VERSION%-bin.zip';" ^
    "Invoke-WebRequest -Uri $url -OutFile '%MAVEN_ZIP%';" ^
    "Expand-Archive -LiteralPath '%MAVEN_ZIP%' -DestinationPath '%MAVEN_BASE%' -Force"
  if errorlevel 1 exit /b 1
)

call "%MAVEN_HOME%\bin\mvn.cmd" %*
endlocal
