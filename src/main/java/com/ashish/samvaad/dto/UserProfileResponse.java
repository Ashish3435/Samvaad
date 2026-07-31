package com.ashish.samvaad.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;

    private String fullName;

    private String email;

    private String profileImageBase64;

    private String aboutStatus;
}