@echo off
echo 🚀 Building Docker image for QuanLyNhaSach...

REM Build the Docker image
docker build -t quanlynhasach:latest .

if %errorlevel% equ 0 (
    echo ✅ Docker build successful!
    echo.
    echo 🐳 To run the container locally:
    echo docker run -p 8080:8080 --env-file .env quanlynhasach:latest
    echo.
    echo 📝 To test on Render, push to GitHub and follow DEPLOYMENT_GUIDE.md
) else (
    echo ❌ Docker build failed!
    pause
    exit /b 1
)

pause
