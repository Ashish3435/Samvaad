package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.ChatMessage;
import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;
import com.ashish.samvaad.service.MessageService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;

    public ChatController(
            SimpMessagingTemplate messagingTemplate,
            MessageService messageService) {

        this.messagingTemplate = messagingTemplate;
        this.messageService = messageService;
    }

    @MessageMapping("/sendMessage")
    public void send(
            ChatMessage chatMessage,
            Principal principal) {

        System.out.println("========================================");
        System.out.println("MESSAGE RECEIVED");
        System.out.println("========================================");

        if (principal == null) {

            System.out.println("❌ PRINCIPAL IS NULL");
            return;
        }

        String senderEmail = principal.getName();

        System.out.println("SENDER : " + senderEmail);
        System.out.println("ROOM : " + chatMessage.getRoomCode());
        System.out.println("CONTENT : " + chatMessage.getContent());

        MessageRequest request = MessageRequest.builder()
                .roomCode(chatMessage.getRoomCode())
                .content(chatMessage.getContent())
                .build();

        MessageResponse response =
                messageService.sendMessage(
                        request,
                        senderEmail
                );

        messagingTemplate.convertAndSend(
                "/topic/room/" + chatMessage.getRoomCode(),
                response
        );

        System.out.println("✅ MESSAGE SENT SUCCESSFULLY");
        System.out.println("========================================");
    }
}