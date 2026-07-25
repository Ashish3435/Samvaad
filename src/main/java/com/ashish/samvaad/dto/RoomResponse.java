package com.ashish.samvaad.dto;

import lombok.*;

import java.util.List;

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

    private List<RoomMemberResponse> members;
}