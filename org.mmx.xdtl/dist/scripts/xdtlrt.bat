@echo off

SETLOCAL ENABLEDELAYEDEXPANSION

set LIB_DIR=%~dp0\xdtl
set CLASSPATH=%LIB_DIR%\resources

for %%f in (%LIB_DIR%\*.jar) do set CLASSPATH=!CLASSPATH!;%%f

java org.mmx.xdtl.cli.Main %*

ENDLOCAL
