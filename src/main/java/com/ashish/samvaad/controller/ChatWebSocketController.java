package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;
import com.ashish.samvaad.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(
            MessageService messageService,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(
            MessageRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        MessageResponse response =
                messageService.sendMessage(
                        request,
                        email
                );

        messagingTemplate.convertAndSend(
                "/topic/" + request.getRoomCode(),
                response
        );
    }

    @MessageMapping("/chat.typing")
    public void typing(
            Map<String, Object> request,
            Authentication authentication
    ) {

        String roomCode =
                (String) request.get("roomCode");

        Map<String, Object> response =
                new HashMap<>();

        response.put(
                "email",
                authentication.getName()
        );

        response.put(
                "typing",
                request.get("typing")
        );

        messagingTemplate.convertAndSend(
                "/topic/" + roomCode + "/typing",
                response
        );
    }
}