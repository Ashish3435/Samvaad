package com.ashish.samvaad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageResponse {

    private Long id;

    private String content;

    private String senderName;

    private String senderEmail;

    private String roomCode;

    private LocalDateTime sentAt;

    private boolean seen;

}