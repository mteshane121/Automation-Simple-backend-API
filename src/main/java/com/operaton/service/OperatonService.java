package com.operaton.service;

import com.operaton.model.UserRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

/**
 * SERVICE — Contains all the business logic for talking to Operaton.
 * This is where all the REST API calls to Operaton happen.
 *
 * Steps it handles:
 * 1. Start the Create New User process
 * 2. Complete Fill In Details task
 * 3. Approve the user
 * 4. Complete service tasks (Create Account + Send Email)
 */
@Service
public class OperatonService {

    @Value("${operaton.base-url}")
    private String baseUrl;

    @Value("${operaton.username}")
    private String username;

    @Value("${operaton.password}")
    private String password;

    private final HttpClient client = HttpClient.newHttpClient();

    // ── Auth Header ──────────────────────────────────
    private String authHeader() {
        String creds = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(creds.getBytes());
    }

    // ── GET Request ──────────────────────────────────
    private String get(String endpoint) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + endpoint))
            .header("Authorization", authHeader())
            .header("Content-Type", "application/json")
            .GET()
            .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    // ── POST Request ─────────────────────────────────
    private String post(String endpoint, String body) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(baseUrl + endpoint))
            .header("Authorization", authHeader())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    // ── Extract JSON value ───────────────────────────
    private String extractValue(String json, String key) {
        String search = "\"" + key + "\":\"";
        int start = json.indexOf(search);
        if (start == -1) return null;
        start += search.length();
        int end = json.indexOf("\"", start);
        return json.substring(start, end);
    }

    // ════════════════════════════════════════════════
    // MAIN METHOD — runs the full workflow
    // ════════════════════════════════════════════════
    public String createUser(UserRequest user) throws Exception {

        StringBuilder log = new StringBuilder();
        log.append("Starting Create New User workflow for: ").append(user.getFullName()).append("\n");

        // STEP 1: Start the process
        String startBody = String.format(
            "{\"variables\":{" +
            "\"fullName\":{\"value\":\"%s\",\"type\":\"String\"}," +
            "\"email\":{\"value\":\"%s\",\"type\":\"String\"}," +
            "\"department\":{\"value\":\"%s\",\"type\":\"String\"}," +
            "\"role\":{\"value\":\"%s\",\"type\":\"String\"}}}",
            user.getFullName(), user.getEmail(),
            user.getDepartment(), user.getRole()
        );

        String startResponse = post("/process-definition/key/create-new-user/start", startBody);
        String processId = extractValue(startResponse, "id");
        log.append("Process started. ID: ").append(processId).append("\n");
        Thread.sleep(1000);

        // STEP 2: Complete Fill In Details
        String taskResponse = get("/task?processDefinitionKey=create-new-user");
        String taskId = extractValue(taskResponse, "id");
        post("/task/" + taskId + "/complete", "{}");
        log.append("Fill In Details completed\n");
        Thread.sleep(1000);

        // STEP 3: Approve the user
        String approvalResponse = get("/task?processDefinitionKey=create-new-user");
        String approvalId = extractValue(approvalResponse, "id");
        post("/task/" + approvalId + "/complete",
            "{\"variables\":{\"approved\":{\"value\":true,\"type\":\"Boolean\"}}}");
        log.append("User approved\n");
        Thread.sleep(1000);

        // STEP 4: Complete service tasks
        for (int i = 0; i < 2; i++) {
            String extTasks = get("/external-task?processDefinitionKey=create-new-user");
            String extId = extractValue(extTasks, "id");
            String topic = extractValue(extTasks, "topicName");
            if (extId != null) {
                post("/external-task/" + extId + "/lock",
                    "{\"workerId\":\"spring-worker\",\"lockDuration\":10000}");
                post("/external-task/" + extId + "/complete",
                    "{\"workerId\":\"spring-worker\"}");
                log.append("Completed service task: ").append(topic).append("\n");
                Thread.sleep(1000);
            }
        }

        log.append("🎉 Workflow completed successfully for ").append(user.getFullName());
        return log.toString();
    }
}