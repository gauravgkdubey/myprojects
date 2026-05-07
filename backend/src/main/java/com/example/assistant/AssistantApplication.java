package com.example.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the AI Assistant Spring Boot application.
 * This class serves as the entry point for the application, bootstrapping
 * the Spring context and starting the embedded web server.
 *
 * The application provides a REST API for interacting with an AI model
 * via Ollama, allowing users to send prompts and receive responses.
 */
@SpringBootApplication
public class AssistantApplication {

    /**
     * Main method that starts the Spring Boot application.
     * This method delegates to SpringApplication.run() to initialize
     * the application context and start the embedded Tomcat server.
     *
     * @param args command-line arguments passed to the application
     */
    public static void main(String[] args) {
        SpringApplication.run(AssistantApplication.class, args);
    }
}

