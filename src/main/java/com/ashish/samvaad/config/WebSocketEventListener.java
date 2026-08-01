package com.ashish.samvaad.config;

import com.ashish.samvaad.service.UserService;
import jakarta.annotation.PreDestroy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class WebSocketEventListener {

    private static final long OFFLINE_GRACE_PERIOD_SECONDS = 10;

    private final UserService userService;

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(2);

    private final Map<String, ScheduledFuture<?>> pendingOfflineTasks =
            new ConcurrentHashMap<>();

    public WebSocketEventListener(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void handleConnected(SessionConnectedEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() instanceof Authentication authentication) {

            String email = authentication.getName();

            System.out.println("CONNECTED : " + email);

            /* CANCEL ANY PENDING "MARK OFFLINE" TASK —
               this connection is a reconnect within the grace period */
            ScheduledFuture<?> pendingTask =
                    pendingOfflineTasks.remove(email);

            if (pendingTask != null) {
                pendingTask.cancel(false);
            }

            userService.updateStatus(email, true);
        }
    }

    @EventListener
    public void handleDisconnect(SessionDisconnectEvent event) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(event.getMessage());

        if (accessor.getUser() instanceof Authentication authentication) {

            String email = authentication.getName();

            System.out.println("DISCONNECTED : " + email);

            /* DELAY MARKING OFFLINE — gives a brief network
               drop or page navigation time to reconnect before
               flipping the status, avoiding online/offline flicker */
            ScheduledFuture<?> task = scheduler.schedule(() -> {
                userService.updateStatus(email, false);
                pendingOfflineTasks.remove(email);
            }, OFFLINE_GRACE_PERIOD_SECONDS, TimeUnit.SECONDS);

            pendingOfflineTasks.put(email, task);

        } else {

            System.out.println("Anonymous WebSocket Disconnect");
        }
    }

    @PreDestroy
    public void shutdown() {
        scheduler.shutdownNow();
    }
}