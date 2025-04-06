package edu.nipun.taskmanager.service.impl;

import edu.nipun.taskmanager.dto.UserDTO;
import edu.nipun.taskmanager.entity.User;
import edu.nipun.taskmanager.exception.ResourceNotFoundException;
import edu.nipun.taskmanager.exception.UserAlreadyExistsException;
import edu.nipun.taskmanager.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDTO userDTO;
    private User user;
    private User savedUser;
    private UserDTO savedUserDTO;
    private final String username = "tester";
    private final String password = "password123";
    private final String encodedPassword = "encoded_password";

    @BeforeEach
    void setUp() {
        userDTO = new UserDTO();
        userDTO.setUsername(username);
        userDTO.setPassword(password);

        user = new User();
        user.setUsername(username);
        user.setPassword(password);

        savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername(username);
        savedUser.setPassword(encodedPassword);

        savedUserDTO = new UserDTO();
        savedUserDTO.setId(1L);
        savedUserDTO.setUsername(username);
    }

    @Test
    @DisplayName("Register user successfully")
    void registerUser_Success() {
        when(userRepository.existsByUsername(username)).thenReturn(false);
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(savedUserDTO);

        UserDTO result = userService.registerUser(userDTO);

        assertEquals(savedUserDTO.getId(), result.getId());
        assertEquals(username, result.getUsername());
        assertNull(result.getPassword());

        verify(userRepository, times(1)).existsByUsername(username);
        verify(modelMapper, times(1)).map(userDTO, User.class);
        verify(bCryptPasswordEncoder, times(1)).encode(password);
        verify(userRepository, times(1)).save(user);
        verify(modelMapper, times(1)).map(savedUser, UserDTO.class);
    }

    @Test
    @DisplayName("Register user with existing username throws exception")
    void registerUser_ExistingUsername_ThrowsException() {
        when(userRepository.existsByUsername(username)).thenReturn(true);
        Exception exception = assertThrows(UserAlreadyExistsException.class,
                () -> userService.registerUser(userDTO));

        assertEquals("Username already exists: " + username, exception.getMessage());

        verify(userRepository, times(1)).existsByUsername(username);
        verifyNoMoreInteractions(modelMapper, bCryptPasswordEncoder, userRepository);
    }

    @Test
    @DisplayName("Get user by username successfully")
    void getUserByUsername_Success() {
        when(userRepository.existsByUsername(username)).thenReturn(true);
        when(userRepository.findByUsername(username)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(savedUserDTO);

        UserDTO result = userService.getUserByUsername(username);

        assertEquals(savedUserDTO.getId(), result.getId());
        assertEquals(username, result.getUsername());
        assertNull(result.getPassword());

        verify(userRepository, times(1)).existsByUsername(username);
        verify(userRepository, times(1)).findByUsername(username);
        verify(modelMapper, times(1)).map(savedUser, UserDTO.class);
    }

    @Test
    @DisplayName("Get user by non-existent username throws exception")
    void getUserByUsername_NonExistentUsername_ThrowsException() {
        when(userRepository.existsByUsername(username)).thenReturn(false);

        Exception exception = assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserByUsername(username));

        assertEquals("Username not found: " + username, exception.getMessage());

        verify(userRepository, times(1)).existsByUsername(username);
        verifyNoMoreInteractions(userRepository, modelMapper);
    }

    @Test
    @DisplayName("Password is encoded during user registration")
    void registerUser_PasswordIsEncoded() {
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(modelMapper.map(userDTO, User.class)).thenReturn(user);
        when(bCryptPasswordEncoder.encode(password)).thenReturn(encodedPassword);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(modelMapper.map(savedUser, UserDTO.class)).thenReturn(savedUserDTO);

        userService.registerUser(userDTO);

        verify(bCryptPasswordEncoder, times(1)).encode(password);
        assertEquals(encodedPassword, user.getPassword());
    }
}