package com.ashish.samvaad.service;

import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;

import java.util.List;

public interface MessageService {

    MessageResponse sendMessage(
            MessageRequest request,
            String senderEmail
    );

    List<MessageResponse> getMessages(
            String roomCode
    );

    void markMessagesAsSeen(
            String roomCode,
            String viewerEmail
    );
}