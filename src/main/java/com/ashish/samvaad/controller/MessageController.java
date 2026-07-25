package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;
import com.ashish.samvaad.service.MessageService;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;


    public MessageController(
            MessageService messageService
    ) {

        this.messageService =
                messageService;
    }


    @PostMapping
    public MessageResponse sendMessage(
            @RequestBody MessageRequest request,
            Authentication authentication
    ) {

        return messageService.sendMessage(
                request,
                authentication.getName()
        );
    }


    @GetMapping("/{roomCode}")
    public List<MessageResponse> getMessages(
            @PathVariable String roomCode
    ) {

        return messageService.getMessages(
                roomCode
        );
    }


    @PutMapping("/{roomCode}/seen")
    public void markMessagesAsSeen(
            @PathVariable String roomCode,
            Authentication authentication
    ) {

        messageService.markMessagesAsSeen(
                roomCode,
                authentication.getName()
        );
    }
}