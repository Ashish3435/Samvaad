package com.ashish.samvaad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // For private chat
    private String otherUserName;

    private String otherUserEmail;

    private List<RoomMemberResponse> members;
}