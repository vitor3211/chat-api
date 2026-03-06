package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.entity.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class RoomService {

    private final RoomRepository roomRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    public RoomService(RoomRepository roomRepository, MessageRepository messageRepository, UserRepository userRepository, TokenService tokenService) {
        this.roomRepository = roomRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.tokenService = tokenService;
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "rooms", key = "@tokenService.getId(#authentication)"),
                    @CacheEvict(value = "rooms", key = "#contactId")
            }
    )
    public Room createNewRoom( String contactId, Authentication authentication) {

        String userId = tokenService.getId(authentication);
        User contact = userRepository.findById(UUID.fromString(contactId)).orElseThrow(
                () ->  new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found.")
        );

        String first = userId.compareTo(contactId) < 0 ? userId : contactId;
        String second = userId.compareTo(contactId) < 0 ? contactId : userId;

        if(first.equals(second)) throw new ResponseStatusException(HttpStatus.CONFLICT, "Invalid id!");
        Optional<Room> existingRoom = roomRepository.findByUser1AndUser2(first, second);
        if (existingRoom.isPresent()) throw new ResponseStatusException(HttpStatus.CONFLICT,"Contact already is in use");

        Room room = new Room();
        room.setUser1(first);
        room.setUser2(second);
        roomRepository.save(room);
        return room;
    }

    @Cacheable(value = "rooms", key = "@tokenService.getId(#authentication)")
    public List<Room> getRoomsByUserId(Authentication authentication) {
        String userId = tokenService.getId(authentication);
        return roomRepository.findByUser1OrUser2(userId, userId);
    }

    public List<Message> getMessages(String roomId, int page, int size, Authentication authentication) {
        String userId = tokenService.getId(authentication);
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (!room.getUser1().equals(userId) && !room.getUser2().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not part of this room");
        }

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("sentAt").ascending());
        return messageRepository.findByRoomId(roomId, pageRequest);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "rooms", key = "@tokenService.getId(#authentication)"),
                    @CacheEvict(value = "rooms", key = "#contactId")
            }
    )
    public void deleteRoom(String contactId, Authentication authentication){
        String userId = tokenService.getId(authentication);
        Room room = roomRepository.findByUser1AndUser2(userId, contactId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found!")
        );
        roomRepository.delete(room);
    }

    public void deleteMessage(String messageId, Authentication authentication){
        Message message = messageRepository.findById(messageId).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Message not found!")
        );

        String senderId = tokenService.getId(authentication);
        if(!(senderId.equals(message.getSenderId())))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You can only delete your own messages!");

        messageRepository.delete(message);
    }
}
