package com.ashish.samvaad.service;

import com.ashish.samvaad.dto.AuthResponse;
import com.ashish.samvaad.dto.LoginRequest;
import com.ashish.samvaad.dto.RegisterRequest;
import com.ashish.samvaad.dto.UpdateAboutStatusRequest;
import com.ashish.samvaad.dto.UpdatePhotoRequest;
import com.ashish.samvaad.dto.UserProfileResponse;
import com.ashish.samvaad.dto.UserStatusResponse;

import java.util.List;

public interface UserService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);

    void updateStatus(String email, boolean online);

    List<UserStatusResponse> getOnlineUsers();

    List<UserStatusResponse> getAllUsers();

    List<UserStatusResponse> searchUsers(String keyword);

    UserProfileResponse getMyProfile(String email);

    UserProfileResponse updatePhoto(String email, UpdatePhotoRequest request);

    UserProfileResponse updateAboutStatus(String email, UpdateAboutStatusRequest request);
}