package com.example.ams.service.impl;

import com.example.ams.dao.UserRepository;
import com.example.ams.entities.Role;
import com.example.ams.entities.User;
import com.example.ams.form.request.UserRequestDTO;
import com.example.ams.form.response.UserResponseDTO;
import com.example.ams.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponseDTO saveUser(UserRequestDTO userRequestDTO) {
        try {

            if (userRequestDTO == null ||
                    userRequestDTO.getName() == null || userRequestDTO.getName().trim().isEmpty() ||
                    userRequestDTO.getEmail() == null || userRequestDTO.getEmail().trim().isEmpty() ||
                    userRequestDTO.getPassword() == null || userRequestDTO.getPassword().trim().isEmpty() ||
                    userRequestDTO.getRole() == null || userRequestDTO.getRole().trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid request: All fields are required.");
            }

            if (userRepository.findByEmail(userRequestDTO.getEmail()).isPresent()) {
                throw new RuntimeException("Email already in use");
            }

            User user = new User();
            user.setName(userRequestDTO.getName());
            user.setEmail(userRequestDTO.getEmail());
            user.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

            try {
                user.setRole(Role.valueOf(userRequestDTO.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role specified.");
            }

            User savedUser = userRepository.save(user);

            return new UserResponseDTO(
                    savedUser.getId().toString(),
                    savedUser.getName(),
                    savedUser.getEmail(),
                    savedUser.getRole().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error saving user: " + e.getMessage());
        }
    }

    @Override
    public List<UserResponseDTO> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            if (users.isEmpty()) {
                throw new RuntimeException("No users found.");
            }

            return users.stream()
                    .map(user -> new UserResponseDTO(
                            user.getId().toString(),
                            user.getName(),
                            user.getEmail(),
                            user.getRole().toString()
                    ))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException("Error fetching users: " + e.getMessage());
        }
    }

    @Override
    public UserResponseDTO getUserById(String id) {
        try {
            Optional<User> user = userRepository.findById(Long.valueOf(id));
            if (user.isEmpty()) {
                throw new RuntimeException("User not found.");
            }

            return new UserResponseDTO(
                    user.get().getId().toString(),
                    user.get().getName(),
                    user.get().getEmail(),
                    user.get().getRole().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error fetching user: " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(String id) {
        try {
            if (!userRepository.existsById(Long.valueOf(id))) {
                throw new RuntimeException("User not found.");
            }
            userRepository.deleteById(Long.valueOf(id));
        } catch (Exception e) {
            throw new RuntimeException("Error deleting user: " + e.getMessage());
        }
    }

    @Override
    public UserResponseDTO updateUser(String id, UserRequestDTO userRequestDTO) {
        try {
            if(userRequestDTO == null ||
            userRequestDTO.getName() == null || userRequestDTO.getName().trim().isEmpty() ||
            userRequestDTO.getEmail() == null || userRequestDTO.getEmail().trim().isEmpty() ||
            userRequestDTO.getRole() == null || userRequestDTO.getRole().trim().isEmpty() ||
            userRequestDTO.getPassword() == null || userRequestDTO.getPassword().trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid request : All field are required.");
            }

            User existingUser = userRepository.findById(Long.parseLong(id))
                    .orElseThrow(() -> new RuntimeException("User not found."));

            Optional<User> userWithEmail = userRepository.findByEmail(userRequestDTO.getEmail());
            if(userWithEmail.isPresent() && !userWithEmail.get().getId().equals(existingUser.getId())) {
                throw  new RuntimeException("Email is already in use by another user.");
            }

            existingUser.setName(userRequestDTO.getName());
            existingUser.setName(userRequestDTO.getEmail());
            existingUser.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));

            try {
                existingUser.setRole(Role.valueOf(userRequestDTO.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid role specified");
            }

            User updatesUser = userRepository.save(existingUser);

            return new UserResponseDTO(
                    updatesUser.getId().toString(),
                    updatesUser.getName(),
                    updatesUser.getEmail(),
                    updatesUser.getRole().toString()
            );
        } catch (Exception e) {
            throw new RuntimeException("Error updating: " + e.getMessage());
        }
    }
}
