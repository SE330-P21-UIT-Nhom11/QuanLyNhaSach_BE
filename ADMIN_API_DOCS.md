# Admin API - Create User Account

## Endpoint: POST /api/admin/users

### Mô tả
API để admin tạo tài khoản user mới với cách thức đơn giản:
- Chỉ cần nhập email
- Role tự động được set là "USER"  
- Password được tự động sinh ra an toàn
- Tên user được lấy từ phần trước @ trong email
- Gửi email chào mừng với thông tin đăng nhập

### Request
```json
{
  "email": "user@example.com"
}
```

### Validation
- Email phải hợp lệ (@Email annotation)
- Email không được trống (@NotBlank annotation)
- Email không được trùng với user đã tồn tại

### Response thành công (201 Created)
```json
{
  "success": true,
  "message": "User account created successfully",
  "user": {
    "id": 123,
    "name": "user",
    "email": "user@example.com", 
    "role": "USER"
  },
  "note": "Welcome email with temporary password has been sent to: user@example.com"
}
```

### Response lỗi (400 Bad Request)
```json
{
  "success": false,
  "error": "INVALID_INPUT",
  "message": "User with email user@example.com already exists"
}
```

### Response lỗi (500 Internal Server Error)
```json
{
  "success": false,
  "error": "CREATION_FAILED", 
  "message": "Failed to create user account: Database connection error"
}
```

### Quyền truy cập
- Yêu cầu: Admin role (@PreAuthorize("hasRole('ADMIN')"))
- Authentication: Bearer token trong header

### Tính năng tự động
1. **Auto-generate password**: Sử dụng PasswordUtil.generateTemporaryPassword()
   - Độ dài 8 ký tự
   - Bao gồm chữ hoa, chữ thường, số, ký tự đặc biệt
   - Mã hóa bằng PasswordEncoder trước khi lưu

2. **Auto-extract name**: Lấy phần trước @ trong email làm tên
   - VD: "user@example.com" → name = "user"

3. **Auto-set role**: Luôn là Role.USER

4. **Auto-send welcome email**: 
   - Email template có thông tin đăng nhập
   - Username và temporary password
   - Hướng dẫn đổi mật khẩu

### Cách test API

#### Với curl:
```bash
curl -X POST "http://localhost:8080/api/admin/users" \
  -H "Authorization: Bearer YOUR_ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"email": "newuser@example.com"}'
```

#### Với Swagger UI:
1. Truy cập http://localhost:8080/swagger-ui.html
2. Tìm section "admin"
3. Expand "POST /api/admin/users"
4. Click "Try it out"
5. Nhập JSON request body
6. Click "Execute"

### Dependencies được sử dụng
- `AdminService.createUserAccount(CreateUserByEmailRequest)`
- `PasswordUtil.generateTemporaryPassword()`
- `EmailService.sendWelcomeEmail()`
- `EmailService.isValidEmail()`
- `UserRepository.save()` và `findByEmail()`
