@echo off
rem Helper script using the system's bundled Maven
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "MVN_CMD="

if not defined MVN_CMD (
    for /d %%D in ("C:\Program Files\JetBrains\IntelliJ IDEA*") do (
        if exist "%%~D\plugins\maven\lib\maven3\bin\mvn.cmd" (
            set "MVN_CMD=%%~D\plugins\maven\lib\maven3\bin\mvn.cmd"
            goto :run
        )
    )
)

:run
if exist "%MVN_CMD%" (
    "%MVN_CMD%" %*
) else (
    where mvn >nul 2>nul
    if not errorlevel 1 (
        mvn %*
    ) else (
        echo Maven was not found. Install Maven or point mvn.bat to a valid Maven installation.
        exit /b 1
    )
)
