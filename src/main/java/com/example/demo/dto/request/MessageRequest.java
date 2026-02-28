package com.example.demo.dto.request;

import java.time.LocalDateTime;

public record MessageRequest(String content, String sender, String roomID, LocalDateTime messageTime) {
}
