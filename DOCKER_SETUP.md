# Docker Setup Guide

## Installing Docker (Optional - for local testing)

### Windows
1. Download Docker Desktop from [docker.com](https://www.docker.com/products/docker-desktop)
2. Install and restart your computer
3. Open Docker Desktop and ensure it's running

### Alternative: Test without Docker
You can deploy directly to Render.com without local Docker testing. Render will build the Docker image for you.

## Testing Locally (if Docker is installed)

### Build and Test
```bash
# Build the Docker image
docker build -t quanlynhasach:latest .

# Run the container
docker run -p 8080:8080 --env-file .env quanlynhasach:latest
```

### Quick Test Scripts
- **Windows**: Run `docker-build.bat`
- **Linux/Mac**: Run `./docker-build.sh`

## Deploy without Local Docker

1. **Push to GitHub**:
   ```bash
   git add .
   git commit -m "Add Docker configuration for Render deployment"
   git push origin main
   ```

2. **Deploy on Render**: Follow `DEPLOYMENT_GUIDE.md`

## Files Created for Deployment

✅ **Dockerfile** - Multi-stage build with security best practices
✅ **.dockerignore** - Optimized for smaller image size
✅ **render.yaml** - Infrastructure as Code for Render
✅ **application-production.properties** - Production configuration
✅ **DEPLOYMENT_GUIDE.md** - Step-by-step deployment guide
✅ **docker-build.sh** / **docker-build.bat** - Local testing scripts

## What's Optimized

### Docker Image
- Multi-stage build (smaller final image)
- Non-root user for security
- Health checks included
- Optimized layer caching

### Spring Boot Configuration
- Production-ready settings
- Connection pooling optimized
- Logging configured for production
- Actuator health checks enabled

### Render.com Integration
- All environment variables configured
- Health checks for uptime monitoring
- Performance optimizations enabled
- SSL/HTTPS automatically handled
