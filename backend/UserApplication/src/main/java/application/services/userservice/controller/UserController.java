package application.services.userservice.controller;

import application.services.userservice.dto.UserRequest;
import application.services.userservice.dto.UserResponse;
import application.services.userservice.exception.ResourceNotFoundException;
import application.services.userservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody UserRequest userRequest) {
        userService.create(userRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<UserResponse> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse get(@PathVariable String id) {
        return userService.get(id);
    }

    @PatchMapping("/{id}/favouriteRecipes")
    @ResponseStatus(HttpStatus.OK)
    public UserResponse updateFavouriteRecipes(@PathVariable String id, @RequestBody List<String> favouriteRecipes) {
        return userService.updateFavouriteRecipes(id, favouriteRecipes);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        userService.delete(id);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
