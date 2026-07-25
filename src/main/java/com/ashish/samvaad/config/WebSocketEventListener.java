package com.ashish.samvaad.config;

import com.ashish.samvaad.service.UserService;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
public class WebSocketEventListener {

    private final UserService userService;

    public WebSocketEventListener(UserService userService) {
        this.userService = userService;
    }


    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());


        if (accessor.getUser() instanceof Authentication authentication) {

            System.out.println(
                    "DISCONNECTED : " + authentication.getName()
            );

            userService.updateStatus(
                    authentication.getName(),
                    false
            );

        } else {

            System.out.println(
                    "Anonymous WebSocket Disconnect"
            );
        }
    }
}