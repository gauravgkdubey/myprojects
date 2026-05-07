package com.example.assistant.dto;

/**
 * Data Transfer Object (DTO) for chat responses.
 * This record represents the structure of outgoing chat responses,
 * containing the AI's reply to the user's prompt.
 *
 * Used by the ChatController to serialize JSON responses.
 */
public record ChatResponse(

    /**
     * The AI's response content generated from the user's prompt.
     */
    String response
) {
}

