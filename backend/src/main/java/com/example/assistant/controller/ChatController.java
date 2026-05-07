package com.example.assistant.controller;

import com.example.assistant.dto.ChatRequest;
import com.example.assistant.dto.ChatResponse;
import com.example.assistant.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling chat-related API requests.
 * This controller exposes endpoints for interacting with the AI assistant,
 * specifically for sending prompts and receiving responses.
 *
 * The controller is mapped to the "/api/chat" path and handles POST requests
 * to process user prompts through the ChatService.
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin
public class ChatController {

    /**
     * Service for handling chat logic and AI interactions.
     * Injected via constructor due to @RequiredArgsConstructor.
     */
    private final ChatService chatService;

    /**
     * Handles POST requests to the chat endpoint.
     * Accepts a ChatRequest containing the user's prompt, processes it
     * through the ChatService, and returns a ChatResponse with the AI's reply.
     *
     * @param request the chat request containing the user's prompt
     * @return a ChatResponse containing the AI's response
     */
    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        return new ChatResponse(chatService.askAI(request.prompt()));
    }
}
