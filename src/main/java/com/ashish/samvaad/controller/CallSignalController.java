package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.CallSignalRequest;
import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.User;
import com.ashish.samvaad.service.RoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class CallSignalController {

    private final SimpMessagingTemplate messagingTemplate;
    private final RoomService roomService;

    public CallSignalController(
            SimpMessagingTemplate messagingTemplate,
            RoomService roomService
    ) {
        this.messagingTemplate = messagingTemplate;
        this.roomService = roomService;
    }

    @MessageMapping("/call.signal")
    public void handleCallSignal(
            CallSignalRequest request,
            Authentication authentication
    ) {

        String senderEmail = authentication.getName();

        boolean isBroadcastType =
                "call-invite".equals(request.getType()) ||
                        "call-end".equals(request.getType());

        if (isBroadcastType) {

            /* SEND TO EVERY OTHER MEMBER OF THE ROOM —
               used for starting or ending a call */
            Room room = roomService.getRoom(request.getRoomCode());

            for (User member : room.getMembers()) {

                if (member.getEmail().equalsIgnoreCase(senderEmail)) {
                    continue;
                }

                messagingTemplate.convertAndSendToUser(
                        member.getEmail(),
                        "/queue/call",
                        withSender(request, senderEmail)
                );
            }

        } else {

            /* PEER-TO-PEER SIGNALING —
               accept / reject / webrtc-offer / webrtc-answer / ice-candidate
               go to exactly one target user */
            if (request.getTargetEmail() == null) {
                return;
            }

            messagingTemplate.convertAndSendToUser(
                    request.getTargetEmail(),
                    "/queue/call",
                    withSender(request, senderEmail)
            );
        }
    }

    private CallSignalRequest withSender(
            CallSignalRequest request,
            String senderEmail
    ) {

        /* STAMP THE SENDER'S EMAIL SO THE RECEIVING CLIENT
           KNOWS WHO THIS SIGNAL CAME FROM */
        CallSignalRequest stamped = new CallSignalRequest();
        stamped.setRoomCode(request.getRoomCode());
        stamped.setType(request.getType());
        stamped.setTargetEmail(senderEmail);
        stamped.setCallType(request.getCallType());
        stamped.setPayload(request.getPayload());

        return stamped;
    }
}