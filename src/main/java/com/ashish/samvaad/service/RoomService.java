package com.ashish.samvaad.service;

import com.ashish.samvaad.dto.CreatePrivateRoomRequest;
import com.ashish.samvaad.dto.CreateRoomRequest;
import com.ashish.samvaad.dto.RoomResponse;
import com.ashish.samvaad.dto.SidebarResponse;
import com.ashish.samvaad.entity.Room;

import java.util.List;

public interface RoomService {

    Room createRoom(CreateRoomRequest request);

    List<Room> getAllRooms();

    Room getRoom(String roomCode);

    RoomResponse createPrivateRoom(CreatePrivateRoomRequest request);

    List<RoomResponse> getMyRooms();

    SidebarResponse getSidebarData();

}