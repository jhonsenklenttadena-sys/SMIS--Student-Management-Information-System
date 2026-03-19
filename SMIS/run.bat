@echo off
echo ================================
echo  SMIS - Build and Run
echo ================================
echo.
echo Step 1: Building project...
call mvn clean package -q
if %errorlevel% neq 0 (
    echo.
    echo BUILD FAILED. See errors above.
    pause
    exit /b 1
)
echo Build successful!
echo.
echo Step 2: Launching SMIS...
echo Login with: admin / admin123
echo.
java --module-path target/dependency --add-modules javafx.controls,javafx.fxml -jar target/StudentMIS-1.0.jar
pause
