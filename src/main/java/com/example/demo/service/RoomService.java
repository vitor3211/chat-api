package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.repository.RoomRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public Room createNewRoom(String id){
        if(roomRepository.findByRoomID(id) != null){
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Room already exists!");
        }

        Room room = new Room();
        room.setRoomID(id);
        roomRepository.save(room);
        return room;
    }

    public Room joinRoom(String id){
        Room room = roomRepository.findByRoomID(id);
        if(room == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!");
        }
        return room;
    }

    public List<Message> getMessages(String id, int page, int size){
        Room room = roomRepository.findByRoomID(id);
        if(room == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "");
        }

        List<Message> messages = room.getMessages();
        int start = Math.max(0, messages.size() - (page + 1) * size);
        int end = Math.min(messages.size(), start + size);
        messages = messages.subList(start, end);

        return messages;

    }
}
