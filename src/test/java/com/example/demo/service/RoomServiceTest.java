package com.example.demo.service;

import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.entity.User;
import com.example.demo.repository.MessageRepository;
import com.example.demo.repository.RoomRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class RoomServiceTest {

    @InjectMocks
    private RoomService roomService;

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenService tokenService;

    private Authentication authentication;

    private String userId = "userId";

    private String contactId = "contactId";

    @BeforeEach
    void setup(){
        authentication = mock(Authentication.class);
        lenient().when(tokenService.getId(authentication)).thenReturn(userId);
    }

    @Test
    void shouldCreateNewRoom(){
        contactId = String.valueOf(UUID.randomUUID());
        User contact = new User();

        when(userRepository.findById(UUID.fromString(contactId))).thenReturn(Optional.of(contact));
        when(roomRepository.findByUser1AndUser2(anyString(), anyString())).thenReturn(Optional.empty());

        Room response = roomService.createNewRoom(contactId, authentication);

        assertNotNull(response);
        verify(roomRepository, times(1)).save(any());
    }

    @Test
    void shouldThrowExceptionWhenRoomAlreadyExists(){
        User contact = new User();
        contactId = String.valueOf(UUID.randomUUID());
        when(tokenService.getId(authentication)).thenReturn(contactId);

        when(userRepository.findById(UUID.fromString(contactId))).thenReturn(Optional.of(contact));
        assertThrows(ResponseStatusException.class, () -> roomService.createNewRoom(contactId, authentication));

    }

    @Test
    void shouldGetMessagesForUser(){
        String roomId = "roomId";
        Room room = new Room();
        room.setUser1(userId);
        room.setUser2(contactId);
        int page = 0;
        int size = 20;

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(messageRepository.findByRoomId(eq(roomId), any(PageRequest.class))).thenReturn(List.of(new Message()));

        List<Message> response = roomService.getMessages(roomId, page, size, authentication);

        assertNotNull(response);
        assertFalse(response.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenRoomIsNotFoundOnDelete(){
        when(roomRepository.findByUser1AndUser2(userId, contactId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> roomService.deleteRoom(contactId, authentication));
    }

    @Test
    void shouldGetRoomsForUser(){
        List<Room> rooms = new ArrayList<>();

        when(roomRepository.findByUser1OrUser2(userId, userId)).thenReturn(rooms);

        List<Room> response = roomService.getRoomsByUserId(authentication);

        assertEquals(rooms, response);
    }

    @Test
    void shouldDeleteRoom(){
        Room room = new Room();

        when(roomRepository.findByUser1AndUser2(userId, contactId)).thenReturn(Optional.of(room));

        roomService.deleteRoom(contactId, authentication);

        verify(roomRepository).delete(room);
    }

    @Test
    void shouldDeleteMessageForCorrectUser(){
        User user = new User();
        user.setId(UUID.randomUUID());
        String messageId = String.valueOf(UUID.randomUUID());
        Message message = new Message();
        message.setId("messageId");
        message.setSenderId(String.valueOf(user.getId()));

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));
        when(tokenService.getId(authentication)).thenReturn(String.valueOf(user.getId()));

        roomService.deleteMessage(messageId, authentication);

        verify(messageRepository, times(1)).delete(message);
    }

    @Test
    void shouldThrowExceptionWhenDeletingOtherUserMessage(){
        String messageId = String.valueOf(UUID.randomUUID());
        Message message = new Message();
        message.setId("messageId");

        when(messageRepository.findById(messageId)).thenReturn(Optional.of(message));

        assertThrows(ResponseStatusException.class, () -> roomService.deleteMessage(messageId, authentication));

        verify(messageRepository, never()).delete(any());
    }

    @Test
    void shouldThrowExceptionForMessageNotFound(){
        String messageId = "messageId";

        when(messageRepository.findById(messageId)).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> roomService.deleteMessage(messageId, authentication));
    }

    @Test
    void shouldThrowExceptionForRoomNotFound(){
        assertThrows(ResponseStatusException.class, () -> roomService.deleteRoom(contactId, authentication));
    }

}