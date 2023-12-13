package application.services.userservice.service;

import application.services.userservice.dto.UserRequest;
import application.services.userservice.dto.UserResponse;
import application.services.userservice.exception.ResourceNotFoundException;
import application.services.userservice.model.User;
import application.services.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void create(UserRequest userRequest) {
        User user = User.builder()
                .googleId(UUID.randomUUID().toString()) // TODO: Replace with Google ID
                .email(userRequest.getEmail())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .favouriteRecipes(Collections.emptyList())
                .build();

        userRepository.save(user);
    }

    public UserResponse get(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + id)
        );

        return mapToUserResponse(user);
    }

    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();

        return users.stream().map(this::mapToUserResponse).toList();
    }

    public UserResponse updateFavouriteRecipes(String id, List<String> favouriteRecipes) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + id)
        );

        user.setFavouriteRecipes(favouriteRecipes);
        userRepository.save(user);

        return mapToUserResponse(user);
    }

    public void delete(String id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("User not found with id: " + id)
        );

        userRepository.delete(user);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .favouriteRecipes(user.getFavouriteRecipes())
                .build();
    }
}
