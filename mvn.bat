@echo off
rem Maven execution helper for Kasnet Online Internship Portal

if not defined JAVA_HOME (
    if exist "C:\Program Files\Java\jdk-24" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-24"
    ) else if exist "C:\Program Files\Java\jdk-21" (
        set "JAVA_HOME=C:\Program Files\Java\jdk-21"
    )
)

if exist "%USERPROFILE%\.maven\apache-maven-3.9.9\bin\mvn.cmd" (
    "%USERPROFILE%\.maven\apache-maven-3.9.9\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

if exist "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" (
    "C:\Program Files\JetBrains\IntelliJ IDEA 2026.2.0.1\plugins\maven-plugin\lib\maven3\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

if exist "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.1\plugins\maven\lib\maven3\bin\mvn.cmd" (
    "C:\Program Files\JetBrains\IntelliJ IDEA Community Edition 2024.1\plugins\maven\lib\maven3\bin\mvn.cmd" %*
    exit /b %ERRORLEVEL%
)

mvn %*
exit /b %ERRORLEVEL%
