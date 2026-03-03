package com.example.demo.controller;

import com.example.demo.dto.request.RoomRequest;
import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService){
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Room> createOrGetRoom(
            @Valid @RequestBody RoomRequest contactId,
            Authentication authentication
    ){
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createNewRoom(contactId.contactId(), authentication));
    }

    @GetMapping
    public ResponseEntity<List<Room>> getUserRooms(Authentication authentication){
        return ResponseEntity.ok(roomService.getRoomsByUserId(authentication));
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Authentication authentication
    ){
        return ResponseEntity.ok(roomService.getMessages(roomId, page, size, authentication));
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> deleteRoom(
            @PathVariable("contactId") String contactId,
            Authentication authentication
    ){
        roomService.deleteRoom(contactId, authentication);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @PathVariable("messageId") String messageId,
            Authentication authentication
    ){
        roomService.deleteMessage(messageId, authentication);
        return ResponseEntity.noContent().build();
    }

}
