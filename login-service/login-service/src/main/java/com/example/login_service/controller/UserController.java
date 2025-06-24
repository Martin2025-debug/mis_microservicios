package com.login.controller;

import com.login.dto.*;
import com.login.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        UserResponse userResponse = userService.registerUser(request);
        ApiResponse response = new ApiResponse(true, "Usuario registrado exitosamente", userResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> loginUser(@Valid @RequestBody UserLoginRequest request) {
        UserResponse userResponse = userService.loginUser(request);
        ApiResponse response = new ApiResponse(true, "Login exitoso", userResponse);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        ApiResponse response = new ApiResponse(true, "Usuario eliminado exitosamente");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long userId) {
        UserResponse userResponse = userService.getUserById(userId);
        ApiResponse response = new ApiResponse(true, "Usuario encontrado", userResponse);
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        ApiResponse response = new ApiResponse(true, "Usuarios obtenidos exitosamente", users);
        return ResponseEntity.ok(response);
    }
}
