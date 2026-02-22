package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoomRepository extends MongoRepository<Room, String> {

    Room findByRoomID(String roomID);

}
