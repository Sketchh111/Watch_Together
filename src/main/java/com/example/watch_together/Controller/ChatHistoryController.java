package com.example.watch_together.Controller;

import com.example.watch_together.Model.ChatEntity;
import com.example.watch_together.Model.Room;
import com.example.watch_together.Repository.ChatRepository;
import com.example.watch_together.Repository.RoomRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class ChatHistoryController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RoomRepository roomRepository;


    @GetMapping("/{roomCode}/messages")
    public ResponseEntity<?> getChatHistory(
            @PathVariable String roomCode) {

        // 1. Find room
        Room room = roomRepository
                .findByRoomCode(roomCode)
                .orElse(null);

        if (room == null) {
            return ResponseEntity
                    .badRequest()
                    .body("Room not found");
        }

        // 2. Find messages for this room
        List<ChatEntity> messages =
                chatRepository.findByRoomOrderBySentAtAsc(room);

        // 3. Return messages
        return ResponseEntity.ok(messages);
    }
}