package com.example.assistant.service;

import com.example.assistant.client.OllamaClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final OllamaClient ollamaClient;

    public String askAI(String prompt) {
        return ollamaClient.sendPrompt(prompt);
    }
}
