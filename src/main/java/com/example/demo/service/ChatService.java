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

//    public Message sendMessage(MessageRequest messageRequest, String roomID){
//        Room room = roomRepository.findByRoomID(roomID);
//
//        Message message = new Message();
//        message.setContent(messageRequest.content());
//        message.setSender(messageRequest.content());
//        message.setSentAt(LocalDateTime.now());
//
//        if(room != null){
//            room.getMessages().add(message);
//            roomRepository.save(room);
//        } else{
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Room Not found");
//        }
//
//        return message;
//    }

    public Message sendMessage(String senderId, String roomId, MessageRequest messageRequest) {
        // 1. Busca a sala pelo ID (69a2f...)
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        // 2. Valida se o senderId (que virá do React) faz parte da sala
        if (!room.getUser1().equals(senderId) && !room.getUser2().equals(senderId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this room");
        }

        // 3. Cria a entidade para o MongoDB
        Message message = new Message();
        message.setRoomId(roomId);
        message.setSenderId(senderId);
        message.setContent(messageRequest.content()); // .content() do seu Record
        message.setSentAt(LocalDateTime.now()); // Essencial para a auditoria/exibição

        return messageRepository.save(message);
    }
}
