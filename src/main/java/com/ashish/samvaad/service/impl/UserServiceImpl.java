package com.ashish.samvaad.service.impl;

import com.ashish.samvaad.dto.AuthResponse;
import com.ashish.samvaad.dto.LoginRequest;
import com.ashish.samvaad.dto.RegisterRequest;
import com.ashish.samvaad.dto.UpdateAboutStatusRequest;
import com.ashish.samvaad.dto.UpdatePhotoRequest;
import com.ashish.samvaad.dto.UserProfileResponse;
import com.ashish.samvaad.dto.UserStatusResponse;
import com.ashish.samvaad.entity.Role;
import com.ashish.samvaad.entity.User;
import com.ashish.samvaad.entity.UserStatus;
import com.ashish.samvaad.exception.EmailAlreadyExistsException;
import com.ashish.samvaad.repository.UserRepository;
import com.ashish.samvaad.service.UserService;
import com.ashish.samvaad.security.CustomUserDetailsService;
import com.ashish.samvaad.security.JwtService;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(
                    "Email already registered. Please login instead."
            );
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .status(UserStatus.OFFLINE)
                .build();

        userRepository.save(user);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                "User Registered Successfully",
                token
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Invalid email or password")
                );

        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPassword()
        )) {
            throw new RuntimeException("Invalid email or password");
        }

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(user.getEmail());

        String token = jwtService.generateToken(userDetails);

        return new AuthResponse(
                "Login Successful",
                token
        );
    }

    @Override
    public void updateStatus(String email, boolean online) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        user.setStatus(
                online
                        ? UserStatus.ONLINE
                        : UserStatus.OFFLINE
        );

        userRepository.save(user);
    }

    @Override
    public List<UserStatusResponse> getOnlineUsers() {

        return userRepository
                .findByStatus(UserStatus.ONLINE)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserStatusResponse> getAllUsers() {

        return userRepository
                .findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public List<UserStatusResponse> searchUsers(String keyword) {

        return userRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        keyword,
                        keyword
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public UserProfileResponse getMyProfile(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updatePhoto(
            String email,
            UpdatePhotoRequest request
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        user.setProfileImageBase64(request.getProfileImageBase64());

        userRepository.save(user);

        return mapToProfileResponse(user);
    }

    @Override
    @Transactional
    public UserProfileResponse updateAboutStatus(
            String email,
            UpdateAboutStatusRequest request
    ) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        user.setAboutStatus(request.getAboutStatus());

        userRepository.save(user);

        return mapToProfileResponse(user);
    }

    private UserStatusResponse mapToResponse(User user) {

        return new UserStatusResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getStatus()
        );
    }

    private UserProfileResponse mapToProfileResponse(User user) {

        return new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getProfileImageBase64(),
                user.getAboutStatus()
        );
    }
}