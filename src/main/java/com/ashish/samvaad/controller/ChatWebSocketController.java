package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;
import com.ashish.samvaad.service.MessageService;
import com.ashish.samvaad.service.RoomService;
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
    private final RoomService roomService;

    public ChatWebSocketController(
            MessageService messageService,
            SimpMessagingTemplate messagingTemplate,
            RoomService roomService
    ) {
        this.messageService = messageService;
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(
            MessageRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        boolean allowed =
                roomService.canSendMessage(
                        request.getRoomCode(),
                        email
                );

        if (!allowed) {
            System.out.println(
                    "BLOCKED: " + email +
                            " tried to send a message in a channel without admin rights"
            );
            return;
        }

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

    @MessageMapping("/chat.seen")
    public void markAsSeen(
            Map<String, String> request,
            Authentication authentication
    ) {

        String roomCode = request.get("roomCode");

        messageService.markMessagesAsSeen(
                roomCode,
                authentication.getName()
        );
    }
}