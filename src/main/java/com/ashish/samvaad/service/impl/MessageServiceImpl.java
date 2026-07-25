package com.ashish.samvaad.service.impl;

import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;
import com.ashish.samvaad.dto.MessageSeenResponse;
import com.ashish.samvaad.entity.Message;
import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.User;
import com.ashish.samvaad.repository.MessageRepository;
import com.ashish.samvaad.repository.RoomRepository;
import com.ashish.samvaad.repository.UserRepository;
import com.ashish.samvaad.service.MessageService;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl
        implements MessageService {

    private final MessageRepository messageRepository;

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    private final SimpMessagingTemplate messagingTemplate;


    public MessageServiceImpl(
            MessageRepository messageRepository,
            RoomRepository roomRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate
    ) {

        this.messageRepository =
                messageRepository;

        this.roomRepository =
                roomRepository;

        this.userRepository =
                userRepository;

        this.messagingTemplate =
                messagingTemplate;
    }


    @Override
    public MessageResponse sendMessage(
            MessageRequest request,
            String email
    ) {

        User sender =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        Room room =
                roomRepository
                        .findByRoomCode(
                                request.getRoomCode()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );


        Message message =
                Message.builder()
                        .content(
                                request.getContent()
                        )
                        .sender(sender)
                        .room(room)
                        .sentAt(
                                LocalDateTime.now()
                        )
                        .seen(false)
                        .build();


        Message savedMessage =
                messageRepository.save(message);


        return mapToResponse(
                savedMessage
        );
    }


    @Override
    public List<MessageResponse> getMessages(
            String roomCode
    ) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );


        return messageRepository
                .findByRoomOrderBySentAtAsc(room)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }


    @Override
    @Transactional
    public void markMessagesAsSeen(
            String roomCode,
            String viewerEmail
    ) {

        Room room =
                roomRepository
                        .findByRoomCode(roomCode)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Room not found"
                                )
                        );


        User viewer =
                userRepository
                        .findByEmail(viewerEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );


        List<Message> unreadMessages =
                messageRepository
                        .findByRoomAndSenderNotAndSeenFalse(
                                room,
                                viewer
                        );


        if (
                unreadMessages.isEmpty()
        ) {

            return;
        }


        for (
                Message message :
                unreadMessages
        ) {

            message.setSeen(true);
        }


        messageRepository.saveAll(
                unreadMessages
        );


        MessageSeenResponse seenResponse =
                MessageSeenResponse.builder()
                        .roomCode(roomCode)
                        .seenBy(viewerEmail)
                        .build();


        messagingTemplate.convertAndSend(
                "/topic/" +
                        roomCode +
                        "/seen",

                seenResponse
        );
    }


    private MessageResponse mapToResponse(
            Message message
    ) {

        return MessageResponse.builder()
                .id(
                        message.getId()
                )
                .content(
                        message.getContent()
                )
                .senderName(
                        message.getSender()
                                .getFullName()
                )
                .senderEmail(
                        message.getSender()
                                .getEmail()
                )
                .roomCode(
                        message.getRoom()
                                .getRoomCode()
                )
                .sentAt(
                        message.getSentAt()
                )
                .seen(
                        message.isSeen()
                )
                .build();
    }
}