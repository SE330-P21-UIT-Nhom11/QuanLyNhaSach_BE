# QuanLyNhaSach Backend

## 📖 Giới thiệu
Backend API cho hệ thống quản lý nhà sách, xây dựng bằng Spring Boot.

## 🚀 Tech Stack
- **Java 17+**
- **Spring Boot 3.4.5**
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database ORM
- **MySQL** - Database
- **JWT** - Token-based authentication
- **Swagger** - API Documentation
- **Maven** - Dependency Management

## 🔧 Environment Setup

### Prerequisites
- Java 17+
- Maven 3.9+
- MySQL 8.0+

### Environment Variables (.env)
```env
# Application
APP_NAME=quanlynhasach
SERVER_PORT=8080

# Database
DB_URL=jdbc:mysql://your-host:3306/your-database
DB_USERNAME=your-username
DB_PASSWORD=your-password
DB_DRIVER=com.mysql.cj.jdbc.Driver

# Database Pool
DB_POOL_SIZE=10
DB_MIN_IDLE=5
DB_CONNECTION_TIMEOUT=20000
DB_IDLE_TIMEOUT=300000

# JPA
JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false
JPA_FORMAT_SQL=true
JPA_OPEN_IN_VIEW=false

# JWT
JWT_SECRET=your-secret-key-minimum-32-characters
JWT_ACCESS_EXPIRATION=900000
JWT_REFRESH_EXPIRATION=2592000000
JWT_RESET_EXPIRATION=300000

# SSL
SSL_TRUSTSTORE=classpath:ssl/truststore.jks
SSL_TRUSTSTORE_PASSWORD=changeit
```

## 🏃‍♂️ Running the Application

1. **Clone repository**
2. **Copy environment file**:
   ```bash
   cp .env.example .env
   ```
3. **Update .env** with your database credentials
4. **Run application**:
   ```bash
   ./mvnw.cmd spring-boot:run
   ```

## 📚 API Documentation

### Base URL
- Development: `http://localhost:8080`

### Swagger UI
- URL: `http://localhost:8080/swagger-ui.html`
- API Docs: `http://localhost:8080/api-docs`

## 🔐 Authentication

### JWT Token System
- **Access Token**: Short-lived (15 minutes), used for API authentication
- **Refresh Token**: Long-lived (30 days), stored in httpOnly cookie
- **Reset Token**: Very short-lived (5 minutes), used for password reset

### Auto Token Refresh
- Middleware automatically refreshes expired access tokens
- New tokens returned in `Authorization` header
- No need to call `/refresh` endpoint manually

### Login Flow
1. POST `/api/auth/login` with credentials
2. Receive access token in response
3. Store access token for subsequent requests
4. Refresh token stored automatically in cookie

## 🛡️ Authorization

### Role-based Access Control
- **USER**: Basic user permissions
- **ADMIN**: Full system access
- **MANAGER**: Management-level permissions

### Endpoint Permissions
- Public: Product listings, categories, authors
- Protected: User management, orders, reviews
- Admin-only: User administration, system config

## 📁 Project Structure

```
src/main/java/com/example/quanlynhasach/
├── config/          # Configuration classes
├── controller/      # REST Controllers
├── dto/            # Data Transfer Objects
├── middleware/     # Custom middlewares
├── model/          # JPA Entities
├── repository/     # Data repositories
├── service/        # Business logic
├── util/           # Utility classes
└── QuanlynhasachApplication.java
```

## 🗄️ Database Schema

### Core Entities
- **User**: System users with roles
- **Product**: Books/products catalog
- **Category**: Product categories
- **Author**: Book authors
- **Publisher**: Book publishers
- **Order**: Customer orders
- **Cart**: Shopping cart functionality
- **Review**: Product reviews

## 🔧 Development

### Code Style
- Follow Java naming conventions
- Use meaningful variable/method names
- Add proper documentation for public APIs
- Handle exceptions appropriately

### Testing
```bash
./mvnw.cmd test
```

### Building
```bash
./mvnw.cmd clean package
```

## 📝 License
This project is licensed under the MIT License.

