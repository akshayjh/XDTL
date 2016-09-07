@echo off

SETLOCAL ENABLEDELAYEDEXPANSION

set LIB_DIR=%~dp0\lib
set CLASSPATH=%~dp0\resources

for %%f in ("%LIB_DIR%\*.jar") do set CLASSPATH=!CLASSPATH!;%%f

set XDTL_OPTS=
if "%XDTL_HOME%" NEQ "" set XDTL_OPTS="-home=%XDTL_HOME%"

echo "%XDTL_LOG_LEVEL%"
rem default loglevel and appender
if "%XDTL_LOG_LEVEL%" == "" set XDTL_LOG_LEVEL=INFO
if "%XDTL_LOG_APPENDER%" == "" SET XDTL_LOG_APPENDER=CONSOLE

if "%XDTL_LOG_APPENDER%" == "FILE" (
                if "%XDTL_LOG_FILE%" == "" set XDTL_LOG_FILE=xdtl.log
)

set XDTL_JAVA_OPTIONS=-DxdtlRootLogger=%XDTL_LOG_LEVEL%,%XDTL_LOG_APPENDER% %XDTL_JAVA_OPTIONS%
if "%XDTL_LOG_FILE%" NEQ "" set XDTL_JAVA_OPTIONS=-DxdtlLogFile=%XDTL_LOG_FILE% %XDTL_JAVA_OPTIONS%

java %XDTL_JAVA_OPTIONS% org.mmx.xdtl.cli.Main %XDTL_OPTS% %*

ENDLOCAL
