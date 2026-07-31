package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.UpdateAboutStatusRequest;
import com.ashish.samvaad.dto.UpdatePhotoRequest;
import com.ashish.samvaad.dto.UserProfileResponse;
import com.ashish.samvaad.dto.UserStatusResponse;
import com.ashish.samvaad.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/status")
    public List<UserStatusResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/online")
    public List<UserStatusResponse> getOnlineUsers() {
        return userService.getOnlineUsers();
    }

    @GetMapping("/search")
    public List<UserStatusResponse> searchUsers(
            @RequestParam String keyword
    ) {
        return userService.searchUsers(keyword);
    }

    @GetMapping("/me")
    public UserProfileResponse getMyProfile(
            Authentication authentication
    ) {
        return userService.getMyProfile(
                authentication.getName()
        );
    }

    @PutMapping("/me/photo")
    public UserProfileResponse updatePhoto(
            @RequestBody UpdatePhotoRequest request,
            Authentication authentication
    ) {
        return userService.updatePhoto(
                authentication.getName(),
                request
        );
    }

    @PutMapping("/me/about")
    public UserProfileResponse updateAboutStatus(
            @RequestBody UpdateAboutStatusRequest request,
            Authentication authentication
    ) {
        return userService.updateAboutStatus(
                authentication.getName(),
                request
        );
    }
}