package com.example.assistant.controller;

import com.example.assistant.dto.ChatRequest;
import com.example.assistant.dto.ChatResponse;
import com.example.assistant.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin
public class ChatController {
private final ChatService chatService;
@PostMapping
public ChatResponse chat(@RequestBody ChatRequest request) {
return new ChatResponse(chatService.askAI(request.prompt()));
}
}