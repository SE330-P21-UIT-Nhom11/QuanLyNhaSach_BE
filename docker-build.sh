#!/bin/bash

# Script to test Docker build locally before deploying to Render

echo "🚀 Building Docker image for QuanLyNhaSach..."

# Build the Docker image
docker build -t quanlynhasach:latest .

if [ $? -eq 0 ]; then
    echo "✅ Docker build successful!"
    echo ""
    echo "🐳 To run the container locally:"
    echo "docker run -p 8080:8080 --env-file .env quanlynhasach:latest"
    echo ""
    echo "📝 To test on Render, push to GitHub and follow DEPLOYMENT_GUIDE.md"
else
    echo "❌ Docker build failed!"
    exit 1
fi
