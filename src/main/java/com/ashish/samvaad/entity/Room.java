package com.ashish.samvaad.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String roomCode;

    @Column(nullable = false)
    private String roomName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType roomType;

    @ManyToOne
    @JoinColumn(name = "created_by")
    private User createdBy;

    @ManyToMany
    @JoinTable(
            name = "room_members",
            joinColumns = @JoinColumn(
                    name = "room_id"
            ),
            inverseJoinColumns = @JoinColumn(
                    name = "user_id"
            )
    )
    @Builder.Default
    private Set<User> members =
            new HashSet<>();

    private LocalDateTime createdAt;
}