package com.ashish.samvaad.security;


import com.ashish.samvaad.service.UserService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.List;


@Component
public class JwtChannelInterceptor implements ChannelInterceptor {


    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;
    private final UserService userService;


    public JwtChannelInterceptor(
            JwtService jwtService,
            CustomUserDetailsService customUserDetailsService,
            UserService userService) {

        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
        this.userService = userService;
    }



    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {


        StompHeaderAccessor accessor =
                StompHeaderAccessor.getAccessor(
                        message,
                        StompHeaderAccessor.class
                );


        if (accessor == null) {
            return message;
        }



        if (StompCommand.CONNECT.equals(accessor.getCommand())) {


            System.out.println("====================================");
            System.out.println("STOMP CONNECT REQUEST");
            System.out.println("====================================");



            List<String> authHeaders =
                    accessor.getNativeHeader("Authorization");



            System.out.println(
                    "Authorization Header : "
                            + authHeaders
            );



            if (authHeaders != null &&
                    !authHeaders.isEmpty()) {


                String bearer =
                        authHeaders.get(0);



                System.out.println(
                        "Bearer Token : "
                                + bearer
                );



                if (bearer.startsWith("Bearer ")) {


                    String token =
                            bearer.substring(7);



                    System.out.println(
                            "JWT Token : "
                                    + token
                    );



                    if (jwtService.validateToken(token)) {



                        String email =
                                jwtService.extractUsername(token);



                        System.out.println(
                                "Email : "
                                        + email
                        );



                        UserDetails userDetails =
                                customUserDetailsService
                                        .loadUserByUsername(email);




                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        userDetails,
                                        null,
                                        userDetails.getAuthorities()
                                );



                        accessor.setUser(authentication);



                        // IMPORTANT
                        accessor.setLeaveMutable(true);



                        System.out.println(
                                "✅ WebSocket Authenticated : "
                                        + authentication.getName()
                        );



                        userService.updateStatus(
                                authentication.getName(),
                                true
                        );



                    } else {


                        System.out.println(
                                "❌ Invalid JWT Token"
                        );

                    }



                } else {


                    System.out.println(
                            "❌ Authorization header is not Bearer"
                    );

                }



            } else {


                System.out.println(
                        "❌ Authorization header NOT received"
                );

            }

        }



        return message;
    }
}