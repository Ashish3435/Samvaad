package com.ashish.samvaad.repository;

import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.RoomType;
import com.ashish.samvaad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {

    Optional<Room> findByRoomCode(String roomCode);

    boolean existsByRoomCode(String roomCode);

    List<Room> findByRoomType(RoomType roomType);

    List<Room> findByRoomTypeAndMembersContaining(
            RoomType roomType,
            User user
    );
}