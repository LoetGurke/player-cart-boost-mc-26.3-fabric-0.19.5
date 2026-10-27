@echo off
setlocal
set APP_HOME=%~dp0
set "GRADLE_USER_HOME=%APP_HOME%.gradle"
set "INSTALLED_GRADLE=C:\Program Files\Gradle\gradle-9.8.2\bin\gradle.bat"
if exist "%INSTALLED_GRADLE%" (
  call "%INSTALLED_GRADLE%" %*
  exit /b
)
if defined JAVA_HOME (
  set JAVA_EXE=%JAVA_HOME%\bin\java.exe
) else (
  set JAVA_EXE=java.exe
)
"%JAVA_EXE%" -classpath "%APP_HOME%gradle\wrapper\gradle-wrapper.jar" org.gradle.wrapper.GradleWrapperMain %*
exit /b %ERRORLEVEL%
