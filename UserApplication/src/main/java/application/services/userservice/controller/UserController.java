package application.services.userservice.controller;

import application.services.userservice.dto.UserRequest;
import application.services.userservice.dto.UserResponse;
import application.services.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody UserRequest userRequest) {
        userService.create(userRequest);
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public UserResponse getByUserName(@RequestParam String userName) {
        return userService.getByUserName(userName);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam String id) {
        userService.delete(id);
    }
}
