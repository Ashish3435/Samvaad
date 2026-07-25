package com.ashish.samvaad.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SidebarResponse {

    private List<RoomResponse> chats;

    private List<RoomResponse> groups;

    private List<RoomResponse> channels;

}