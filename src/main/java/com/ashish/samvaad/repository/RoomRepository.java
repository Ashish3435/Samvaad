package com.ashish.samvaad.repository;

import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.RoomType;
import com.ashish.samvaad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            SELECT DISTINCT r
            FROM Room r
            LEFT JOIN FETCH r.members
            WHERE r.createdBy = :user
               OR :user MEMBER OF r.members
            """)
    List<Room> findMyRooms(@Param("user") User user);
}