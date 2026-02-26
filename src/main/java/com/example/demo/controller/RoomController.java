package com.example.demo.controller;

import com.example.demo.DTO.response.MessageResponse;
import com.example.demo.entity.Message;
import com.example.demo.entity.Room;
import com.example.demo.service.RoomService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Room> createRoom(@RequestBody String id){
        return ResponseEntity.status(HttpStatus.CREATED).body(roomService.createNewRoom(id));
    }

    @GetMapping("/{roomID}")
    public ResponseEntity<Room> joinRoom(@PathVariable String roomID){
        return ResponseEntity.ok(roomService.joinRoom(roomID));
    }

    @GetMapping("/{roomID}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable String roomID,
                                                     @RequestParam(value = "page", defaultValue = "0", required = false) int page,
                                                     @RequestParam(value = "size", defaultValue = "20", required = false) int size
                                                     ){
        return ResponseEntity.ok(roomService.getMessages(roomID, page, size));
    }

}
