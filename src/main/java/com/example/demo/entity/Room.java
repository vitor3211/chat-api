package com.example.demo.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Document(collection = "rooms")
public class Room {

    @Id
    private String id;
    private String roomID;
    private List<String> participants = new ArrayList<>();
    private List<Message> messages = new ArrayList<>();

}
