package application.services.controller;

import application.services.dto.RecipeRequest;
import application.services.dto.RecipeResponse;
import application.services.exception.ResourceNotFoundException;
import application.services.model.Recipe;
import application.services.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RecipeResponse create(@RequestBody RecipeRequest recipeRequest) {
        return recipeService.create(recipeRequest);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeResponse getById(@PathVariable Long id) {
        return recipeService.getById(id);
    }

    @GetMapping("user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeResponse> getAllForUser(@PathVariable String userId) {
        return recipeService.getAllForUser(userId);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeResponse update(@PathVariable Long id, @RequestBody RecipeRequest recipeRequest) {
        return recipeService.update(id, recipeRequest);
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        recipeService.delete(id);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
