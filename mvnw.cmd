@REM ----------------------------------------------------------------------------
@REM MindMate Maven Wrapper Delegation Script
@REM ----------------------------------------------------------------------------
@echo off
setlocal
set "DIR=%~dp0"
set "MVN_CMD=%DIR%maven\apache-maven-3.9.6\bin\mvn.cmd"

if exist "%MVN_CMD%" (
    "%MVN_CMD%" %*
) else (
    where mvn >nul 2>nul
    if %ERRORLEVEL% equ 0 (
        mvn %*
    ) else (
        echo [ERROR] Maven not found.
        exit /b 1
    )
)
