package com.example.watch_together.Controller; // ⚠️ MUST match your project package

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class SyncVideoController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/sync")
    public void sync(VideoMessage message) {

        System.out.println("Received room: " + message.getRoomId());
        System.out.println("Action: " + message.getAction());

        messagingTemplate.convertAndSend(
                "/topic/video/" + message.getRoomId(),
                message
        );
    }
}