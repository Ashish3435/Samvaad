package com.ashish.samvaad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomResponse {

    private Long id;

    private String roomCode;

    private String roomName;

    private String roomType;

}