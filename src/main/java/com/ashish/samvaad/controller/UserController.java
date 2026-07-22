package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.UserStatusResponse;
import com.ashish.samvaad.service.UserService;
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
}