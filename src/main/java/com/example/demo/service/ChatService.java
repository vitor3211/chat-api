package com.example.demo.service;

import com.example.demo.DTO.request.MessageRequest;
import com.example.demo.DTO.response.MessageResponse;
import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import org.apache.http.protocol.HTTP;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class ChatService {

    private final RoomRepository roomRepository;

    public ChatService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Message sendMessage(MessageRequest messageRequest, String roomID){
        Room room = roomRepository.findByRoomID(roomID);

        Message message = new Message();
        message.setContent(messageRequest.content());
        message.setSender(messageRequest.content());
        message.setSentAt(LocalDateTime.now());

        if(room != null){
            room.getMessages().add(message);
            roomRepository.save(room);
        } else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Room Not found");
        }

        return message;
    }
}
