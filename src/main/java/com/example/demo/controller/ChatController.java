package com.example.demo.controller;

import com.example.demo.DTO.request.MessageRequest;
import com.example.demo.entity.Message;
import com.example.demo.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @MessageMapping("/sendMessage/{roomID}")
    @SendTo("/topic/chat/{roomID}")
    public Message sendMessage(
            @RequestBody MessageRequest messageRequest,
            @DestinationVariable String roomID) throws Exception{
        return chatService.sendMessage(messageRequest, roomID);
    }
}
