package com.example.assistant.dto;

/**
 * Data Transfer Object (DTO) for chat requests.
 * This record represents the structure of incoming chat requests,
 * containing the user's prompt that will be sent to the AI model.
 *
 * Used by the ChatController to deserialize JSON requests.
 */
public record ChatRequest(

    /**
     * The user's input prompt to be processed by the AI.
     */
    String prompt
) {
}

