package com.example.assistant.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class OllamaClient {

    @Value("${ollama.url:http://localhost:11434/v1/chat/completions}")
    private String ollamaUrl;

    @Value("${ollama.models.url:http://localhost:11434/v1/models}")
    private String ollamaModelsUrl;

    @Value("${ollama.model:llama2}")
    private String ollamaModel;

    private final RestTemplate restTemplate = new RestTemplate();

    @SuppressWarnings("unchecked")
    public String sendPrompt(String prompt) {
        Map<String, Object> requestBody = Map.of(
                "model", ollamaModel,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(ollamaUrl, requestBody, Map.class);
            if (response == null) {
                return "No response from Ollama.";
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.get("choices");
            if (choices == null || choices.isEmpty()) {
                return "No choices returned from Ollama.";
            }

            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            if (message == null) {
                return "No message returned from Ollama.";
            }

            return message.getOrDefault("content", "").toString();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == 404 && e.getResponseBodyAsString().contains("model")) {
                String models = getInstalledModels();
                return String.format("Ollama model '%s' not found. Installed models: %s. Set OLLAMA_MODEL to one of these.", ollamaModel, models);
            }
            return String.format("Ollama request failed (%s %s): %s", e.getStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
        }
    }

    @SuppressWarnings("unchecked")
    private String getInstalledModels() {
        try {
            Map<String, Object> response = restTemplate.getForObject(ollamaModelsUrl, Map.class);
            if (response == null) {
                return "<unable to fetch models>";
            }

            Object modelsObj = response.get("models");
            if (!(modelsObj instanceof List)) {
                return "<unexpected model response>";
            }

            List<Map<String, Object>> models = (List<Map<String, Object>>) modelsObj;
            List<String> names = new ArrayList<>();
            for (Map<String, Object> model : models) {
                Object name = model.get("name");
                if (name != null) {
                    names.add(name.toString());
                }
            }
            return String.join(", ", names);
        } catch (Exception ex) {
            return "<unable to fetch models>";
        }
    }
}
