package com.example.assistant.service;

import com.example.assistant.client.OllamaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling chat-related business logic.
 * This service acts as an intermediary between the controller and the
 * AI client, encapsulating the logic for processing user prompts and
 * generating AI responses.
 *
 * It delegates the actual AI interaction to the OllamaClient.
 */
@Service
@RequiredArgsConstructor
public class ChatService {

    /**
     * Client for interacting with the Ollama AI service.
     * Injected via constructor due to @RequiredArgsConstructor.
     */
    private final OllamaClient ollamaClient;

    /**
     * Processes a user prompt and returns an AI-generated response.
     * This method forwards the prompt to the OllamaClient for processing
     * and returns the response from the AI model.
     *
     * @param prompt the user's input prompt
     * @return the AI's response as a string
     */
    public String askAI(String prompt) {
        return ollamaClient.sendPrompt(prompt);
    }
}
