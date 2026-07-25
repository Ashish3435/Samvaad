package com.ashish.samvaad.config;

import com.ashish.samvaad.entity.Room;
import com.ashish.samvaad.entity.RoomType;
import com.ashish.samvaad.repository.RoomRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initialize(RoomRepository roomRepository) {

        return args -> {

            if (!roomRepository.existsByRoomCode("GENERAL")) {

                Room room = Room.builder()
                        .roomCode("GENERAL")
                        .roomName("General")
                        .roomType(RoomType.CHANNEL)
                        .createdAt(LocalDateTime.now())
                        .build();

                roomRepository.save(room);

                System.out.println("✅ General Channel Created");

            }

        };
    }

}