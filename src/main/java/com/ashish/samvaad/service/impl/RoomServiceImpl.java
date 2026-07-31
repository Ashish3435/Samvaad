package com.ashish.samvaad.service.impl;

import com.ashish.samvaad.dto.AddMemberRequest;
import com.ashish.samvaad.dto.CreatePrivateRoomRequest;
import com.ashish.samvaad.dto.CreateRoomRequest;
import com.ashish.samvaad.dto.PromoteRequest;
import com.ashish.samvaad.dto.RemoveMemberRequest;
import com.ashish.samvaad.dto.RoomMemberResponse;
import com.ashish.samvaad.dto.RoomResponse;
import com.ashish.samvaad.dto.SidebarResponse;
import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.RoomType;
import com.ashish.samvaad.entity.User;
import com.ashish.samvaad.repository.RoomRepository;
import com.ashish.samvaad.repository.UserRepository;
import com.ashish.samvaad.service.RoomService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    private final UserRepository userRepository;

    public RoomServiceImpl(
            RoomRepository roomRepository,
            UserRepository userRepository
    ) {
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Room createRoom(
            CreateRoomRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        User user =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        String roomCode =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        Room room =
                Room.builder()
                        .roomCode(roomCode)
                        .roomName(
                                request.getRoomName()
                        )
                        .roomType(
                                RoomType.valueOf(
                                        request.getRoomType()
                                )
                        )
                        .createdBy(user)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        room.getMembers()
                .add(user);

        /* CREATOR BECOMES ADMIN FOR GROUPS AND CHANNELS */
        room.getAdmins()
                .add(user);

        return roomRepository.save(room);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Room> getAllRooms() {

        return roomRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Room getRoom(
            String roomCode
    ) {

        return roomRepository
                .findByRoomCodeWithMembers(roomCode)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Room not found"
                        )
                );
    }

    @Override
    @Transactional
    public RoomResponse createPrivateRoom(
            CreatePrivateRoomRequest request
    ) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        User currentUser =
                userRepository
                        .findByEmail(
                                currentUserEmail
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Current user not found"
                                )
                        );

        User selectedUser =
                userRepository
                        .findByEmail(
                                request.getEmail()
                        )
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Selected user not found"
                                )
                        );

        List<Room> currentUserRooms =
                roomRepository
                        .findByRoomTypeAndMembersContaining(
                                RoomType.CHAT,
                                currentUser
                        );

        Room existingRoom =
                currentUserRooms
                        .stream()
                        .filter(room ->
                                room.getMembers()
                                        .contains(
                                                selectedUser
                                        )
                        )
                        .findFirst()
                        .orElse(null);

        if (existingRoom != null) {

            return mapToRoomResponse(
                    existingRoom
            );
        }

        String roomCode =
                UUID.randomUUID()
                        .toString()
                        .replace("-", "")
                        .substring(0, 8)
                        .toUpperCase();

        String roomName;

        if (
                currentUser
                        .getId()
                        .equals(
                                selectedUser.getId()
                        )
        ) {

            roomName =
                    currentUser.getFullName();

        } else {

            roomName =
                    selectedUser.getFullName();
        }

        Room room =
                Room.builder()
                        .roomCode(roomCode)
                        .roomName(roomName)
                        .roomType(RoomType.CHAT)
                        .createdBy(currentUser)
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .build();

        room.getMembers()
                .add(currentUser);

        if (
                !currentUser
                        .getId()
                        .equals(
                                selectedUser.getId()
                        )
        ) {

            room.getMembers()
                    .add(selectedUser);
        }

        Room savedRoom =
                roomRepository.save(room);

        return mapToRoomResponse(
                savedRoom
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoomResponse> getMyRooms() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String email =
                authentication.getName();

        User currentUser =
                userRepository
                        .findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        return roomRepository
                .findMyRooms(currentUser)
                .stream()
                .map(this::mapToRoomResponse)
                .collect(
                        Collectors.toList()
                );
    }

    @Override
    @Transactional(readOnly = true)
    public SidebarResponse getSidebarData() {

        List<RoomResponse> myRooms =
                getMyRooms();

        List<RoomResponse> chats =
                myRooms
                        .stream()
                        .filter(room ->
                                room.getRoomType()
                                        .equals("CHAT")
                        )
                        .collect(
                                Collectors.toList()
                        );

        List<RoomResponse> groups =
                myRooms
                        .stream()
                        .filter(room ->
                                room.getRoomType()
                                        .equals("GROUP")
                        )
                        .collect(
                                Collectors.toList()
                        );

        List<RoomResponse> channels =
                myRooms
                        .stream()
                        .filter(room ->
                                room.getRoomType()
                                        .equals("CHANNEL")
                        )
                        .collect(
                                Collectors.toList()
                        );

        return SidebarResponse
                .builder()
                .chats(chats)
                .groups(groups)
                .channels(channels)
                .build();
    }

    @Override
    @Transactional
    public RoomResponse addMember(
            String roomCode,
            AddMemberRequest request,
            String requesterEmail
    ) {

        Room room = getRoomOrThrow(roomCode);

        requireAdmin(room, requesterEmail);

        User userToAdd =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User to add not found"
                                )
                        );

        room.getMembers().add(userToAdd);

        Room savedRoom = roomRepository.save(room);

        return mapToRoomResponse(savedRoom);
    }

    @Override
    @Transactional
    public RoomResponse removeMember(
            String roomCode,
            RemoveMemberRequest request,
            String requesterEmail
    ) {

        Room room = getRoomOrThrow(roomCode);

        requireAdmin(room, requesterEmail);

        User userToRemove =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User to remove not found"
                                )
                        );

        room.getMembers().remove(userToRemove);
        room.getAdmins().remove(userToRemove);

        Room savedRoom = roomRepository.save(room);

        return mapToRoomResponse(savedRoom);
    }

    @Override
    @Transactional
    public RoomResponse promoteToAdmin(
            String roomCode,
            PromoteRequest request,
            String requesterEmail
    ) {

        Room room = getRoomOrThrow(roomCode);

        requireAdmin(room, requesterEmail);

        User userToPromote =
                userRepository
                        .findByEmail(request.getEmail())
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User to promote not found"
                                )
                        );

        if (!room.getMembers().contains(userToPromote)) {
            throw new RuntimeException(
                    "User must be a member before becoming an admin"
            );
        }

        room.getAdmins().add(userToPromote);

        Room savedRoom = roomRepository.save(room);

        return mapToRoomResponse(savedRoom);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canSendMessage(
            String roomCode,
            String email
    ) {

        Room room = getRoomOrThrow(roomCode);

        /* ONLY CHANNELS ARE BROADCAST-ONLY —
           CHAT and GROUP allow any member to send */
        if (room.getRoomType() != RoomType.CHANNEL) {
            return true;
        }

        return room.getAdmins()
                .stream()
                .anyMatch(admin ->
                        admin.getEmail().equalsIgnoreCase(email)
                );
    }

    private Room getRoomOrThrow(String roomCode) {

        return roomRepository
                .findByRoomCode(roomCode)
                .orElseThrow(() ->
                        new RuntimeException("Room not found")
                );
    }

    private void requireAdmin(Room room, String requesterEmail) {

        boolean isAdmin =
                room.getAdmins()
                        .stream()
                        .anyMatch(admin ->
                                admin.getEmail().equalsIgnoreCase(requesterEmail)
                        );

        if (!isAdmin) {
            throw new RuntimeException(
                    "Only admins can perform this action"
            );
        }
    }

    private RoomMemberResponse mapToMemberResponse(User user, Room room) {

        boolean isAdmin =
                room.getAdmins()
                        .stream()
                        .anyMatch(admin ->
                                admin.getId().equals(user.getId())
                        );

        return RoomMemberResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .status(user.getStatus().name())
                .admin(isAdmin)
                .build();
    }

    private RoomResponse mapToRoomResponse(Room room) {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        String currentUserEmail =
                authentication.getName();

        List<RoomMemberResponse> members =
                room.getMembers()
                        .stream()
                        .map(user -> mapToMemberResponse(user, room))
                        .toList();

        String otherUserName = room.getRoomName();
        String otherUserEmail = null;

        if (room.getRoomType() == RoomType.CHAT) {

            User otherUser =
                    room.getMembers()
                            .stream()
                            .filter(user ->
                                    !user.getEmail().equalsIgnoreCase(currentUserEmail))
                            .findFirst()
                            .orElse(null);

            if (otherUser != null) {

                otherUserName = otherUser.getFullName();
                otherUserEmail = otherUser.getEmail();

            } else {

                User me =
                        room.getMembers()
                                .stream()
                                .findFirst()
                                .orElse(null);

                if (me != null) {
                    otherUserName = me.getFullName();
                    otherUserEmail = me.getEmail();
                }
            }
        }

        boolean requesterIsAdmin =
                room.getAdmins()
                        .stream()
                        .anyMatch(admin ->
                                admin.getEmail().equalsIgnoreCase(currentUserEmail)
                        );

        return RoomResponse.builder()
                .id(room.getId())
                .roomCode(room.getRoomCode())
                .roomName(room.getRoomName())
                .roomType(room.getRoomType().name())
                .otherUserName(otherUserName)
                .otherUserEmail(otherUserEmail)
                .members(members)
                .isAdmin(requesterIsAdmin)
                .build();
    }
}