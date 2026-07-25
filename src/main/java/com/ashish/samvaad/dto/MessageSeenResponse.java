package com.ashish.samvaad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageSeenResponse {

    private String roomCode;

    private String seenBy;

}