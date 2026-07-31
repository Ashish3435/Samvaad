package com.ashish.samvaad.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomMemberResponse {

    private Long id;

    private String fullName;

    private String email;

    private String status;

    private boolean admin;
}