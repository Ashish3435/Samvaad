package com.ashish.samvaad.repository;

import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.RoomMember;
import com.ashish.samvaad.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomMemberRepository extends JpaRepository<RoomMember, Long> {

    List<RoomMember> findByUser(User user);

    List<RoomMember> findByRoom(Room room);
}