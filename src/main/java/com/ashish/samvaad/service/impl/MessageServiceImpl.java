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
    @Transactional
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


        boolean hasContent =
                request.getContent() != null &&
                        !request.getContent().isBlank();

        boolean hasAttachment =
                request.getAttachmentData() != null &&
                        !request.getAttachmentData().isBlank();

        if (!hasContent && !hasAttachment) {
            throw new RuntimeException(
                    "Message must have content or an attachment"
            );
        }


        LocalDateTime now = LocalDateTime.now();

        Message message =
                Message.builder()
                        .content(
                                request.getContent()
                        )
                        .sender(sender)
                        .room(room)
                        .sentAt(now)
                        .seen(false)
                        .attachmentData(request.getAttachmentData())
                        .attachmentType(request.getAttachmentType())
                        .attachmentName(request.getAttachmentName())
                        .build();


        Message savedMessage =
                messageRepository.save(message);

        room.setLastMessageAt(now);
        roomRepository.save(room);

        MessageResponse response = mapToResponse(savedMessage);

        String content = request.getContent();
        boolean isGroupOrChannel =
                room.getRoomType().name().equals("GROUP") ||
                        room.getRoomType().name().equals("CHANNEL");

        for (User member : room.getMembers()) {

            if (member.getEmail().equalsIgnoreCase(email)) {
                continue;
            }

            boolean mentioned =
                    isGroupOrChannel &&
                            isMentioned(content, member.getFullName());

            MessageResponse notificationPayload =
                    MessageResponse.builder()
                            .id(response.getId())
                            .content(response.getContent())
                            .senderName(response.getSenderName())
                            .senderEmail(response.getSenderEmail())
                            .roomCode(response.getRoomCode())
                            .sentAt(response.getSentAt())
                            .seen(response.isSeen())
                            .mentioned(mentioned)
                            .attachmentData(response.getAttachmentData())
                            .attachmentType(response.getAttachmentType())
                            .attachmentName(response.getAttachmentName())
                            .build();

            messagingTemplate.convertAndSendToUser(
                    member.getEmail(),
                    "/queue/notifications",
                    notificationPayload
            );
        }

        return response;
    }

    private boolean isMentioned(String content, String fullName) {

        if (content == null || fullName == null || fullName.isBlank()) {
            return false;
        }

        String firstName = fullName.trim().split("\\s+")[0];

        String mentionPattern = "@" + firstName;

        return content.toLowerCase().contains(mentionPattern.toLowerCase());
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
                .attachmentData(
                        message.getAttachmentData()
                )
                .attachmentType(
                        message.getAttachmentType()
                )
                .attachmentName(
                        message.getAttachmentName()
                )
                .build();
    }

}