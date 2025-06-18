package com.example.quanlynhasach.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class DotEnvApplicationListener implements ApplicationListener<ApplicationEnvironmentPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
        ConfigurableEnvironment environment = event.getEnvironment();
        
        try {
            System.out.println("DotEnvApplicationListener: Loading .env file...");
            
            Dotenv dotenv = Dotenv.configure()
                    .directory("./")
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();
            
            Map<String, Object> dotenvProperties = new HashMap<>();
            dotenv.entries().forEach(entry -> {
                dotenvProperties.put(entry.getKey(), entry.getValue());
                System.out.println("Loaded: " + entry.getKey() + " = " + entry.getValue());
            });
            
            // Add with high priority
            environment.getPropertySources().addFirst(
                new MapPropertySource("dotenv-listener", dotenvProperties)
            );
            
            System.out.println("DotEnvApplicationListener: Successfully loaded " + dotenvProperties.size() + " variables");
            
        } catch (Exception e) {
            System.err.println("DotEnvApplicationListener: Could not load .env file: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
