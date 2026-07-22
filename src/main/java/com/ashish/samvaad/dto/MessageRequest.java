package com.ashish.samvaad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {

    private String roomCode;

    private String sender;

    private String content;
}