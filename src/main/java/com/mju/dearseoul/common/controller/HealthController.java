package com.mju.dearseoul.common.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
public class HealthController {
    private final ObjectProvider<ChatClient> chatClients;

    public HealthController(ObjectProvider<ChatClient> chatClients) {
        this.chatClients = chatClients;
    }

    @GetMapping
    public Map<String, Object> health() {
        return Map.of("status", "UP", "aiConfigured", chatClients.getIfAvailable() != null);
    }
}
