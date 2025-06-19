# Deployment Guide for Render.com

## Prerequisites

1. **GitHub Repository**: Push your code to a GitHub repository
2. **Render.com Account**: Sign up at [render.com](https://render.com)
3. **TiDB Database**: Your database is already configured and running

## Deployment Steps

### Option 1: Using render.yaml (Recommended)

1. **Push your code to GitHub** with all the files created:
   - `Dockerfile`
   - `.dockerignore`
   - `render.yaml`
   - `application-production.properties`

2. **Connect to Render.com**:
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New +"
   - Select "Blueprint"
   - Connect your GitHub repository
   - Render will automatically detect `render.yaml` and configure everything

### Option 2: Manual Setup

1. **Create a new Web Service**:
   - Go to [Render Dashboard](https://dashboard.render.com)
   - Click "New +"
   - Select "Web Service"
   - Connect your GitHub repository

2. **Configure Build Settings**:
   - **Environment**: Docker
   - **Region**: Singapore (or closest to your users)
   - **Branch**: main
   - **Build Command**: (leave empty, Docker will handle)
   - **Start Command**: (leave empty, Docker will handle)

3. **Set Environment Variables**:
   Copy all variables from the `render.yaml` file:
   ```
   APP_NAME=quanlynhasach
   SERVER_PORT=8080
   SPRING_PROFILES_ACTIVE=production
   DB_URL=jdbc:mysql://gateway01.us-west-2.prod.aws.tidbcloud.com:4000/QuanLyNhaSach_DB?sslMode=VERIFY_IDENTITY
   DB_USERNAME=TGhSTtGU3r6xK2v.root
   DB_PASSWORD=ON5gY2jvPrXRtcFt
   # ... (add all other variables from render.yaml)
   ```

## Important Configuration Notes

### Database Configuration
- ✅ Your TiDB database is already configured
- ✅ Connection string includes SSL mode for security
- ✅ Connection pooling optimized for production

### Security
- ✅ Non-root user in Docker container
- ✅ JWT secret configured
- ✅ HTTPS will be automatically provided by Render

### Email Configuration
- ✅ Gmail SMTP configured
- ⚠️  **Important**: Make sure your Gmail App Password is correct
- ⚠️  **Important**: Verify 2FA is enabled on your Gmail account

### Performance Optimizations
- ✅ Connection pooling configured
- ✅ Hibernate batch processing enabled
- ✅ Compression enabled for responses
- ✅ Health checks configured

## Post-Deployment

### 1. Verify Deployment
After deployment, your application will be available at:
```
https://your-app-name.onrender.com
```

### 2. Test Endpoints
- **Health Check**: `GET /actuator/health`
- **API Documentation**: `GET /swagger-ui.html`
- **Test Authentication**: `POST /api/auth/login`

### 3. Monitor Logs
- Go to your service dashboard on Render
- Click "Logs" to monitor application startup and errors

## Environment Variables Reference

| Variable | Description | Example |
|----------|-------------|---------|
| `APP_NAME` | Application name | `quanlynhasach` |
| `SERVER_PORT` | Port (fixed at 8080 for Render) | `8080` |
| `SPRING_PROFILES_ACTIVE` | Spring profile | `production` |
| `DB_URL` | Database connection string | Your TiDB URL |
| `DB_USERNAME` | Database username | Your TiDB username |
| `DB_PASSWORD` | Database password | Your TiDB password |
| `JWT_SECRET` | JWT signing key | Your secure key |
| `MAIL_USERNAME` | Gmail address | Your Gmail |
| `MAIL_PASSWORD` | Gmail app password | Your app password |

## Troubleshooting

### Common Issues

1. **Build Failure**:
   - Check Docker build logs
   - Ensure all dependencies are in `pom.xml`
   - Verify Java version compatibility

2. **Database Connection Issues**:
   - Verify TiDB credentials
   - Check firewall/security group settings
   - Ensure SSL configuration is correct

3. **Email Not Working**:
   - Verify Gmail app password
   - Check 2FA is enabled
   - Test email credentials locally first

4. **Application Won't Start**:
   - Check environment variables
   - Review application logs
   - Verify all required configs are set

### Health Check Endpoint

The application includes health checks at `/actuator/health`:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "diskSpace": {
      "status": "UP"
    }
  }
}
```

## Cost Considerations

- **Free Tier**: 750 hours/month, sleeps after 15 mins of inactivity
- **Starter Plan**: $7/month, no sleep, better performance
- **Database**: Your TiDB is external, costs separate

## Security Best Practices

1. **Environment Variables**: Never commit sensitive data to git
2. **JWT Secret**: Use a strong, unique secret key
3. **Database**: TiDB with SSL is secure
4. **HTTPS**: Automatically provided by Render
5. **App Passwords**: Use Gmail app passwords, not account password

## Next Steps

1. Set up monitoring and alerting
2. Configure custom domain (optional)
3. Set up CI/CD for automatic deployments
4. Configure backup strategies for your data
5. Monitor performance and optimize as needed
