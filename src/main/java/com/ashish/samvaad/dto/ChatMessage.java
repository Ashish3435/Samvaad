package com.ashish.samvaad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessage {

    private String roomCode;

    private String content;

    private String sender;
}