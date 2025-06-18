package com.example.quanlynhasach.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "DB_URL=jdbc:h2:mem:testdb",
    "DB_USERNAME=sa",
    "DB_PASSWORD=",
    "DB_DRIVER=org.h2.Driver",
    "JWT_SECRET=testSecretKeyForJWT123456789012345678901234567890",
    "JWT_ACCESS_EXPIRATION=900000",
    "JWT_REFRESH_EXPIRATION=2592000000"
})
public class ApplicationStartupTest {

    @Test
    public void contextLoads() {
        // This test will pass if the application context loads successfully
    }
}
