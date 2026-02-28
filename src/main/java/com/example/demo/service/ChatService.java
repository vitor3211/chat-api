package com.example.demo.service;

import com.example.demo.dto.request.MessageRequest;
import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    public ChatService(RoomRepository roomRepository, MessageRepository messageRepository) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
    }

    public Message sendMessage(String senderId, String roomId, MessageRequest messageRequest) {

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (!room.getUser1().equals(senderId) && !room.getUser2().equals(senderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this room");
        }

        Message message = new Message();
        message.setRoomId(roomId);
        message.setSenderId(senderId);
        message.setContent(messageRequest.content());
        message.setSentAt(LocalDateTime.now());

        return messageRepository.save(message);
    }
}
