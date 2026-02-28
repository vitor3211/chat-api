package com.example.demo.controller;

import com.example.demo.dto.request.RoomRequest;
import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
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
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");

        Room room = roomService.createOrGetRoom(userId, contactId.contactId());
        return ResponseEntity.status(HttpStatus.CREATED).body(room);
    }

    @GetMapping
    public ResponseEntity<List<Room>> getUserRooms(Authentication authentication){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");

        List<Room> rooms = roomService.getRoomsByUserId(userId);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<List<Message>> getMessages(
            @PathVariable String roomId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            Authentication authentication
    ){
        Jwt jwt = (Jwt) authentication.getPrincipal();
        String userId = jwt.getClaimAsString("sub");

        List<Message> messages = roomService.getMessages(roomId, userId, page, size);
        return ResponseEntity.ok(messages);
    }

}
