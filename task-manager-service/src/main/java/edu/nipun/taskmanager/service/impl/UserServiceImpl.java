package edu.nipun.taskmanager.service.impl;

import edu.nipun.taskmanager.dto.UserDTO;
import edu.nipun.taskmanager.entity.User;
import edu.nipun.taskmanager.exception.ResourceNotFoundException;
import edu.nipun.taskmanager.exception.UserAlreadyExistsException;
import edu.nipun.taskmanager.repository.UserRepository;
import edu.nipun.taskmanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDTO registerUser(UserDTO userDTO) {
        if (userRepository.existsByUsername(userDTO.getUsername())) {
            throw new UserAlreadyExistsException("Username already exists: " + userDTO.getUsername());
        }

        User user = modelMapper.map(userDTO, User.class);
        user.setPassword(bCryptPasswordEncoder.encode(userDTO.getPassword()));

        User savedUser = userRepository.save(user);
        UserDTO savedUserDTO = modelMapper.map(savedUser, UserDTO.class);
        savedUserDTO.setPassword(null);

        return savedUserDTO;
    }

    @Override
    public UserDTO getUserByUsername(String username) {
        if (!userRepository.existsByUsername(username)) {
            throw new ResourceNotFoundException("Username not found: " + username);
        }
        User user = userRepository.findByUsername(username);
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        userDTO.setPassword(null);
        return userDTO;
    }
}
