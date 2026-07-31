package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.AddMemberRequest;
import com.ashish.samvaad.dto.CreatePrivateRoomRequest;
import com.ashish.samvaad.dto.CreateRoomRequest;
import com.ashish.samvaad.dto.PromoteRequest;
import com.ashish.samvaad.dto.RemoveMemberRequest;
import com.ashish.samvaad.dto.RoomResponse;
import com.ashish.samvaad.dto.SidebarResponse;
import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.service.RoomService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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

    @PostMapping("/{roomCode}/members")
    public ResponseEntity<RoomResponse> addMember(
            @PathVariable String roomCode,
            @RequestBody AddMemberRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                roomService.addMember(
                        roomCode,
                        request,
                        authentication.getName()
                )
        );
    }

    @DeleteMapping("/{roomCode}/members")
    public ResponseEntity<RoomResponse> removeMember(
            @PathVariable String roomCode,
            @RequestBody RemoveMemberRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                roomService.removeMember(
                        roomCode,
                        request,
                        authentication.getName()
                )
        );
    }

    @PostMapping("/{roomCode}/promote")
    public ResponseEntity<RoomResponse> promoteToAdmin(
            @PathVariable String roomCode,
            @RequestBody PromoteRequest request,
            Authentication authentication
    ) {

        return ResponseEntity.ok(
                roomService.promoteToAdmin(
                        roomCode,
                        request,
                        authentication.getName()
                )
        );
    }
}