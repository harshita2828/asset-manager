package com.example.ams.service;

import com.example.ams.datamodels.form.request.UserRequestDTO;
import com.example.ams.datamodels.form.response.UserResponseDTO;
import java.util.List;

public interface UserService {
    UserResponseDTO saveUser(UserRequestDTO userRequestDTO);
    List<UserResponseDTO> getAllUsers();
    UserResponseDTO getUserById(String  id);
    void deleteUser(String id);

    UserResponseDTO updateUser(String id, UserRequestDTO userRequestDTO);
}
