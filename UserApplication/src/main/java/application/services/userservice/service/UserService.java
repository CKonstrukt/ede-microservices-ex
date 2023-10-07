package application.services.userservice.service;

import application.services.userservice.dto.UserRequest;
import application.services.userservice.dto.UserResponse;
import application.services.userservice.model.User;
import application.services.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public void create(UserRequest userRequest) {
        User user = User.builder()
                .userName(userRequest.getUserName())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();

        userRepository.save(user);
    }

    public UserResponse getByUserName(String userName) {
        User user = userRepository.findByUserName(userName);

        return mapToUserResponse(user);
    }

    public List<UserResponse> getAll() {
        List<User> users = userRepository.findAll();

        return users.stream().map(this::mapToUserResponse).toList();
    }

    public void delete(String id) {
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .userName(user.getUserName())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
}
