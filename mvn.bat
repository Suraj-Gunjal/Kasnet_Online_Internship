@echo off
rem Helper script using the system's bundled Maven
set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd"
if exist "%MVN_CMD%" (
    "%MVN_CMD%" %*
) else (
    mvn %*
)
