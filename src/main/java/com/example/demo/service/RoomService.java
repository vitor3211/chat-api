package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;

    public RoomService(RoomRepository roomRepository, MessageRepository messageRepository) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
    }

    public Room createOrGetRoom(String userId, String contactId) {
        String first = userId.compareTo(contactId) < 0 ? userId : contactId;
        String second = userId.compareTo(contactId) < 0 ? contactId : userId;

        Optional<Room> existingRoom = roomRepository.findByUser1AndUser2(first, second);
        if (existingRoom.isPresent()) {
            return existingRoom.get();
        }

        Room room = new Room();
        room.setUser1(first);
        room.setUser2(second);
        roomRepository.save(room);
        return room;
    }

    public List<Room> getRoomsByUserId(String userId) {
        return roomRepository.findByUser1OrUser2(userId, userId);
    }

    public List<Message> getMessages(String roomId, String userId, int page, int size) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (!room.getUser1().equals(userId) && !room.getUser2().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this room");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sentAt").ascending());
        return messageRepository.findByRoomId(roomId, pageRequest);
    }

}
