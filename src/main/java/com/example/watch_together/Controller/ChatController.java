package com.example.watch_together.Controller;

import com.example.watch_together.Model.ChatEntity;
import com.example.watch_together.Model.ChatMessage;
import com.example.watch_together.Model.Room;
import com.example.watch_together.Model.User;

import com.example.watch_together.Repository.ChatRepository;
import com.example.watch_together.Repository.RoomRepository;
import com.example.watch_together.Repository.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @MessageMapping("/chat")
    public void send(ChatMessage message) {

        System.out.println("Chat received");
        System.out.println("Room: " + message.getRoomId());
        System.out.println("Sender: " + message.getSender());
        System.out.println("Message: " + message.getMessage());

        // Save only real messages
        if (!"TYPING".equals(message.getType())) {

            // Find room using room code
            Room room = roomRepository
                    .findByRoomCode(message.getRoomId())
                    .orElse(null);

            if (room == null) {
                System.out.println("Room not found: " + message.getRoomId());
                return;
            }

            // Find user using username
            User user = userRepository
                    .findByUsername(message.getSender())
                    .orElse(null);

            if (user == null) {
                System.out.println("User not found: " + message.getSender());
                return;
            }

            // Create ChatEntity
            ChatEntity chatEntity = new ChatEntity(
                    room,
                    user,
                    message.getMessage(),
                    "MESSAGE");

            ChatEntity savedChat = chatRepository.save(chatEntity);

            message.setSentAt(
                    savedChat.getSentAt().toString());
            System.out.println("Chat saved successfully.");
        }

        // Send message to all members subscribed to this room
        messagingTemplate.convertAndSend(
                "/topic/chat/" + message.getRoomId(),
                message);
    }

}