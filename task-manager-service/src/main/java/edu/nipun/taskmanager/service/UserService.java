package edu.nipun.taskmanager.service;

import edu.nipun.taskmanager.dto.UserDTO;

public interface UserService {
    UserDTO registerUser(UserDTO userDTO);
    UserDTO getUserByUsername(String username);
}
