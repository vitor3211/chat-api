package com.example.demo.controller;

import com.example.demo.dto.request.MessageRequest;
import com.example.demo.entity.Message;
import com.example.demo.entity.User;
import com.example.demo.security.RateLimiter;
import com.example.demo.service.ChatService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/sendMessage/{roomId}")
    @SendTo("/topic/chat/{roomId}")
    @RateLimiter(capacity = 40, refillTokens = 40, refillDurationSeconds = 60)
    public Message sendMessage(
            @DestinationVariable String roomId,
            @Payload MessageRequest messageRequest
    ) throws Exception {
        return chatService.sendMessage(messageRequest.sender(), roomId, messageRequest);
    }
}
