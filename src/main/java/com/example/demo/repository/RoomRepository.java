package com.example.demo.repository;

import com.example.demo.entity.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends MongoRepository<Room, String> {

    Optional<Room> findByUser1AndUser2(String user1, String user2);

    List<Room> findByUser1OrUser2(String user1, String user2);

}
