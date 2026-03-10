package com.operaton.controller;

import com.operaton.model.UserRequest;
import com.operaton.service.EmailService;
import com.operaton.service.OperatonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CONTROLLER — Handles incoming HTTP requests.
 * This is the front door of our API.
 *
 * Endpoints:
 * POST /api/users/create  → triggers the full workflow
 * GET  /api/users/health  → checks if the app is running
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private OperatonService operatonService;

    @Autowired
    private EmailService emailService;

    /**
     * Health check endpoint.
     * Call: GET http://localhost:8080/api/users/health
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok(" Operaton BPM API is running!");
    }

    /**
     * Create a new user — triggers the full Operaton workflow.
     * Call: POST http://localhost:8080/api/users/create
     * Body: { "fullName": "John", "email": "john@test.com", "department": "IT", "role": "Developer" }
     */
    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody UserRequest userRequest) {
        try {
            System.out.println("Received request to create user: " + userRequest.getFullName());

            // Step 1: Run the Operaton workflow
            String workflowLog = operatonService.createUser(userRequest);

            // Step 2: Send welcome email
            emailService.sendWelcomeEmail(userRequest);

            // Step 3: Return success response
            String response = "{\n" +
                "  \"status\": \"SUCCESS\",\n" +
                "  \"message\": \"User " + userRequest.getFullName() + " created successfully!\",\n" +
                "  \"email\": \"Welcome email sent to " + userRequest.getEmail() + "\",\n" +
                "  \"log\": \"" + workflowLog.replace("\n", "\\n") + "\"\n" +
                "}";

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            String error = "{\n" +
                "  \"status\": \"ERROR\",\n" +
                "  \"message\": \"" + e.getMessage() + "\"\n" +
                "}";
            return ResponseEntity.status(500).body(error);
        }
    }
}