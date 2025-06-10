package com.example.ams.controller;

import com.example.ams.datamodels.form.response.APIResponse;
import com.example.ams.datamodels.form.request.UserRequestDTO;
import com.example.ams.datamodels.form.response.UserResponseDTO;
import com.example.ams.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<APIResponse<UserResponseDTO>> createUser(@RequestBody UserRequestDTO request) {
        logger.info("Inside createUser");
        try {
            if (!ObjectUtils.isEmpty(request) || !ObjectUtils.isEmpty(request.getName()) || !ObjectUtils.isEmpty(request.getEmail())
                    || !ObjectUtils.isEmpty(request.getPassword())) {
                logger.error("Invalid request: Name, Email, and Password cannot be null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request: Name, Email, and Password cannot be null", null));
            }

            UserResponseDTO user = userService.saveUser(request);
            logger.info("User created successfully");
            return ResponseEntity.ok(new APIResponse<>(true, "User created successfully", user));
        } catch (Exception e) {
            logger.error("Error creating user: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error creating user: " + e.getMessage(), null));
        }
    }

    @GetMapping
    public ResponseEntity<APIResponse<List<UserResponseDTO>>> getAllUsers() {
        try {
            List<UserResponseDTO> users = userService.getAllUsers();
            if (!ObjectUtils.isEmpty(users) || users.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new APIResponse<>(true, "No users found", null));
            }
            return ResponseEntity.ok(new APIResponse<>(true, "Users fetched successfully", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching users: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponseDTO>> getUserById(@PathVariable("id") String id) {
        try {
            UserResponseDTO user = userService.getUserById(id);
            if (!ObjectUtils.isEmpty(user)) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT)
                        .body(new APIResponse<>(true, "No user found", null));
            }
            return ResponseEntity.ok(new APIResponse<>(true, "User fetched successfully", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error fetching user: " + e.getMessage(), null));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<APIResponse<Void>> deleteUser(@PathVariable("id") String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new APIResponse<>(true, "User deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error deleting user: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<APIResponse<UserResponseDTO>> updateUser(@PathVariable("id") String id, @RequestBody UserRequestDTO userRequestDTO) {
        try {
            if (!ObjectUtils.isEmpty(userRequestDTO) || !ObjectUtils.isEmpty(userRequestDTO.getName()) || !ObjectUtils.isEmpty(userRequestDTO.getEmail())
                    || !ObjectUtils.isEmpty(userRequestDTO.getPassword()) || !ObjectUtils.isEmpty(userRequestDTO.getRole())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new APIResponse<>(false, "Invalid request: Name, Email, Password, and Role cannot be null", null));
            }

            UserResponseDTO updatedUser = userService.updateUser(id, userRequestDTO);
            return ResponseEntity.ok(new APIResponse<>(true, "User updated successfully", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new APIResponse<>(false, "Error updating user: " + e.getMessage(), null));
        }
    }
}
