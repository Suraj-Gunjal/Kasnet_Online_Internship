@echo off
rem Helper script using the system's bundled Maven
set "JAVA_HOME=C:\Program Files\Java\jdk-21"
set "MVN_CMD=C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.1\plugins\maven\lib\maven3\bin\mvn.cmd"
if exist "%MVN_CMD%" (
    "%MVN_CMD%" %*
) else (
    mvn %*
)
