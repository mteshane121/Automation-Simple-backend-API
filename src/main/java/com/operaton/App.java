package com.operaton;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Operaton Spring Boot application.
 * This starts the embedded web server on port 8080.
 */
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
        System.out.println("\n========================================");
        System.out.println("  Operaton BPM API is running!");
        System.out.println("  Open: http://localhost:8080");
        System.out.println("========================================\n");
    }
}