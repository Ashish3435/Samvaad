package com.ashish.samvaad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;

    private LocalDateTime sentAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean seen = false;

    @Column(columnDefinition = "TEXT")
    private String attachmentData;

    private String attachmentType;

    private String attachmentName;
}