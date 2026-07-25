package com.ashish.samvaad.controller;

import com.ashish.samvaad.dto.AuthResponse;
import com.ashish.samvaad.dto.LoginRequest;
import com.ashish.samvaad.dto.RegisterRequest;
import com.ashish.samvaad.service.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return userService.login(request);
    }
}