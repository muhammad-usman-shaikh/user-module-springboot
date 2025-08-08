package com.usman.auth.user_module_springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.usman.auth.user_module_springboot.dto.RegisterUserDTO;
import com.usman.auth.user_module_springboot.model.User;
import com.usman.auth.user_module_springboot.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Register a new user account.
     *
     * @param payload User registration info
     * @return The newly created user
     */
    @PostMapping("/register")
    public ResponseEntity<User> register(@Valid @RequestBody RegisterUserDTO payload) {
        User registeredUser = userService.registerUser(payload);
        return ResponseEntity.ok(registeredUser);
    }

}
