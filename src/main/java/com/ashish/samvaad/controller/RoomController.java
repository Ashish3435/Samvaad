package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.CreatePrivateRoomRequest;
import com.ashish.samvaad.dto.CreateRoomRequest;
import com.ashish.samvaad.dto.RoomResponse;
import com.ashish.samvaad.dto.SidebarResponse;
import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(
            RoomService roomService
    ) {
        this.roomService = roomService;
    }

    @PostMapping
    public ResponseEntity<Room> createRoom(
            @RequestBody CreateRoomRequest request
    ) {

        return ResponseEntity.ok(
                roomService.createRoom(request)
        );
    }

    @GetMapping
    public ResponseEntity<List<Room>> getAllRooms() {

        return ResponseEntity.ok(
                roomService.getAllRooms()
        );
    }

    @GetMapping("/{roomCode}")
    public ResponseEntity<Room> getRoom(
            @PathVariable String roomCode
    ) {

        return ResponseEntity.ok(
                roomService.getRoom(roomCode)
        );
    }

    @GetMapping("/my")
    public ResponseEntity<List<RoomResponse>> getMyRooms() {

        return ResponseEntity.ok(
                roomService.getMyRooms()
        );
    }

    @GetMapping("/sidebar")
    public ResponseEntity<SidebarResponse> getSidebarData() {

        return ResponseEntity.ok(
                roomService.getSidebarData()
        );
    }

    @PostMapping("/private")
    public ResponseEntity<RoomResponse> createPrivateRoom(
            @RequestBody CreatePrivateRoomRequest request
    ) {

        return ResponseEntity.ok(
                roomService.createPrivateRoom(request)
        );
    }
}