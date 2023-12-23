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
                .firstName("firstName")
                .lastName("lastName")
                .favouriteRecipes(List.of("recipe1", "recipe2"))
                .build();

        // Act
        userService.create(userRequest);

        // Assert
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    public void getAll_WithValidRequest_ReturnsUsers() {
        // Arrange
        User user1 = User.builder()
                .email("email1")
                .firstName("firstName1")
                .lastName("lastName1")
                .favouriteRecipes(List.of("recipe1.1", "recipe1.2"))
                .build();

        User user2 = User.builder()
                .email("email2")
                .firstName("firstName2")
                .lastName("lastName2")
                .favouriteRecipes(List.of("recipe2.1", "recipe2.2"))
                .build();
        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<UserResponse> users = userService.getAll();

        // Assert
        assertEquals(2, users.size());
        assertEquals("email1", users.get(0).getEmail());
        assertEquals("firstName1", users.get(0).getFirstName());
        assertEquals("lastName1", users.get(0).getLastName());
        assertEquals(2, users.get(0).getFavouriteRecipes().size());
        assertEquals("recipe1.1", users.get(0).getFavouriteRecipes().get(0));
        assertEquals("recipe1.2", users.get(0).getFavouriteRecipes().get(1));
        assertEquals("email2", users.get(1).getEmail());
        assertEquals("firstName2", users.get(1).getFirstName());
        assertEquals("lastName2", users.get(1).getLastName());
        assertEquals(2, users.get(1).getFavouriteRecipes().size());
        assertEquals("recipe2.1", users.get(1).getFavouriteRecipes().get(0));
        assertEquals("recipe2.2", users.get(1).getFavouriteRecipes().get(1));

        verify(userRepository, times(1)).findAll();
    }

    @Test
    public void getById_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById("id")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> userService.get("id"));

        verify(userRepository, times(1)).findById("id");
    }

    @Test
    public void getById_WithValidRequest_ReturnsUser() {
        // Arrange
        User user = User.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .favouriteRecipes(List.of("recipe1", "recipe2"))
                .build();
        when(userRepository.findById("id")).thenReturn(java.util.Optional.of(user));

        // Act
        UserResponse userResponse = userService.get("id");

        // Assert
        assertEquals("email", userResponse.getEmail());
        assertEquals("firstName", userResponse.getFirstName());
        assertEquals("lastName", userResponse.getLastName());
        assertEquals(2, userResponse.getFavouriteRecipes().size());
        assertEquals("recipe1", userResponse.getFavouriteRecipes().get(0));
        assertEquals("recipe2", userResponse.getFavouriteRecipes().get(1));

        verify(userRepository, times(1)).findById("id");
    }

    @Test
    public void updateFavouriteRecipes_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(userRepository.findById("id")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                userService.updateFavouriteRecipes("id", List.of("recipe1", "recipe2")));

        verify(userRepository, times(1)).findById("id");
    }

    @Test
    public void updateFavouriteRecipes_WithValidRequest_UpdatesUser() {
        // Arrange
        User user = User.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .favouriteRecipes(List.of("recipe1", "recipe2"))
                .build();
        when(userRepository.findById("id")).thenReturn(java.util.Optional.of(user));

        // Act
        UserResponse userResponse = userService.updateFavouriteRecipes("id", List.of("recipe3", "recipe4"));

        // Assert
        assertEquals("email", userResponse.getEmail());
        assertEquals("firstName", userResponse.getFirstName());
        assertEquals("lastName", userResponse.getLastName());
        assertEquals(2, userResponse.getFavouriteRecipes().size());
        assertEquals("recipe3", userResponse.getFavouriteRecipes().get(0));
        assertEquals("recipe4", userResponse.getFavouriteRecipes().get(1));

        verify(userRepository, times(1)).findById("id");
        verify(userRepository, times(1)).save(any(User.class));
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
                .firstName("firstName")
                .lastName("lastName")
                .favouriteRecipes(List.of("recipe1", "recipe2"))
                .build();
        when(userRepository.findById("id")).thenReturn(java.util.Optional.of(user));

        // Act
        userService.delete("id");

        // Assert
        verify(userRepository, times(1)).findById("id");
        verify(userRepository, times(1)).delete(any(User.class));
    }
}
