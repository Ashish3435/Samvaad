package com.ashish.samvaad.service.impl;

import com.ashish.samvaad.dto.MessageRequest;
import com.ashish.samvaad.dto.MessageResponse;
import com.ashish.samvaad.entity.Message;
import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.User;
import com.ashish.samvaad.repository.MessageRepository;
import com.ashish.samvaad.repository.RoomRepository;
import com.ashish.samvaad.repository.UserRepository;
import com.ashish.samvaad.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;

    public MessageServiceImpl(
            MessageRepository messageRepository,
            RoomRepository roomRepository,
            UserRepository userRepository) {

        this.messageRepository = messageRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    public MessageResponse sendMessage(
            MessageRequest request,
            String email) {

        User sender = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Room room = roomRepository.findByRoomCode(request.getRoomCode())
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .room(room)
                .sentAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);

        return MessageResponse.builder()
                .id(savedMessage.getId())
                .content(savedMessage.getContent())
                .senderName(savedMessage.getSender().getFullName())
                .senderEmail(savedMessage.getSender().getEmail())
                .roomCode(savedMessage.getRoom().getRoomCode())
                .sentAt(savedMessage.getSentAt())
                .build();
    }

    @Override
    public List<MessageResponse> getMessages(String roomCode) {

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        return messageRepository.findByRoomOrderBySentAtAsc(room)
                .stream()
                .map(message -> MessageResponse.builder()
                        .id(message.getId())
                        .content(message.getContent())
                        .senderName(message.getSender().getFullName())
                        .senderEmail(message.getSender().getEmail())
                        .roomCode(message.getRoom().getRoomCode())
                        .sentAt(message.getSentAt())
                        .build())
                .toList();
    }

}