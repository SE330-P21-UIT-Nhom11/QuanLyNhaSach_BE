package com.example.quanlynhasach.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotEnvEnvironmentPostProcessor implements EnvironmentPostProcessor {    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        try {
            System.out.println("Loading .env file...");
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
                System.out.println("Loaded env variable: " + entry.getKey() + " = " + entry.getValue());
            });
            
            environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv", dotenvProperties)
            );
            
            System.out.println("Successfully loaded " + dotenvProperties.size() + " environment variables from .env");
        } catch (Exception e) {
            // Nếu không load được .env thì bỏ qua
            System.err.println("Could not load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
