package com.ashish.samvaad.repository;

import com.ashish.samvaad.entity.Message;
import com.ashish.samvaad.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByRoomOrderBySentAtAsc(Room room);

}