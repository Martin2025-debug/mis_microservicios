package com.login.service;

import com.login.dto.UserLoginRequest;
import com.login.dto.UserRegistrationRequest;
import com.login.dto.UserResponse;
import com.login.entity.User;
import com.login.exception.UserNotFoundException;
import com.login.exception.UserAlreadyExistsException;
import com.login.exception.InvalidCredentialsException;
import com.login.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserResponse registerUser(UserRegistrationRequest request) {
        // Verificar si el usuario ya existe
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException("El username ya está en uso");
        }
        
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException("El email ya está en uso");
        }
        
        // Crear nuevo usuario
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        
        User savedUser = userRepository.save(user);
        
        return convertToUserResponse(savedUser);
    }
    
    public UserResponse loginUser(UserLoginRequest request) {
        // Buscar usuario por username o email
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .or(() -> userRepository.findByEmail(request.getUsernameOrEmail()))
                .orElseThrow(() -> new InvalidCredentialsException("Credenciales inválidas"));
        
        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciales inválidas");
        }
        
        return convertToUserResponse(user);
    }
    
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
        
        userRepository.delete(user);
    }
    
    public UserResponse getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Usuario no encontrado con ID: " + userId));
        
        return convertToUserResponse(user);
    }
    
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToUserResponse)
                .collect(Collectors.toList());
    }
    
    private UserResponse convertToUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }
}