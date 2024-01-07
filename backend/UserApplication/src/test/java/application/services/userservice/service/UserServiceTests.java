package application.services.userservice.service;

import application.services.userservice.dto.UserRequest;
import application.services.userservice.dto.UserResponse;
import application.services.userservice.exception.ResourceNotFoundException;
import application.services.userservice.model.User;
import application.services.userservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    public void create_WithValidRequest_SavesUser() {
        // Arrange
        UserRequest userRequest = UserRequest.builder()
                .email("email")
                .name("name")
                .image("image")
                .build();

        // Act
        UserResponse user = userService.create(userRequest);

        // Assert
        assertEquals("email", user.getEmail());
        assertEquals("name", user.getName());
        assertEquals("image", user.getImage());

        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getAll_WithValidRequest_ReturnsUsers() {
        // Arrange
        User user1 = User.builder()
                .email("email1")
                .name("name1")
                .image("image1")
                .build();

        User user2 = User.builder()
                .email("email2")
                .name("name2")
                .image("image2")
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserResponse> users = userService.getAll();

        // Assert
        assertEquals(2, users.size());
        assertEquals("email1", users.get(0).getEmail());
        assertEquals("name1", users.get(0).getName());
        assertEquals("image1", users.get(0).getImage());
        assertEquals("email2", users.get(1).getEmail());
        assertEquals("name2", users.get(1).getName());
        assertEquals("image2", users.get(1).getImage());

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getById_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById("id")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.getById("id"));

        verify(userRepository, times(1)).findById("id");
    }

    @Test
    public void getById_WithValidRequest_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .email("email")
                .name("name")
                .image("image")
                .build();
        when(userRepository.findById("id")).thenReturn(java.util.Optional.of(user));

        // Act
        UserResponse userResponse = userService.getById("id");

        // Assert
        assertEquals("email", userResponse.getEmail());
        assertEquals("name", userResponse.getName());
        assertEquals("image", userResponse.getImage());

        verify(userRepository, times(1)).findById("id");
    }

    @Test
    public void delete_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById("id")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.delete("id"));

        verify(userRepository, times(1)).findById("id");
    }

    @Test
    public void delete_WithValidRequest_DeletesUser() {
        // Arrange
        User user = User.builder()
                .email("email")
                .name("name")
                .image("image")
                .build();
        when(userRepository.findById("id")).thenReturn(java.util.Optional.of(user));

        // Act
        userService.delete("id");

        // Assert
        verify(userRepository, times(1)).findById("id");
        verify(userRepository, times(1)).delete(any(User.class));
    }
}
