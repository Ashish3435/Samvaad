package com.ashish.samvaad.dto;

import com.ashish.samvaad.entity.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatusResponse {

    private Long id;

    private String fullName;

    private String email;

    private UserStatus status;
}