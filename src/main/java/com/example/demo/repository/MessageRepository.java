package com.example.demo.repository;

import com.example.demo.entity.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {

    List<Message> findByRoomId(String roomId, Pageable pageable);
}
