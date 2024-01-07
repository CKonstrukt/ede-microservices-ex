package application.services.controller;

import application.services.dto.RecipeRequest;
import application.services.dto.RecipeResponse;
import application.services.exception.DelimiterInInstructionException;
import application.services.exception.ResourceNotFoundException;
import application.services.model.Recipe;
import application.services.service.RecipeService;
import jakarta.servlet.http.HttpServletRequest;
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
    public RecipeResponse create(@RequestBody RecipeRequest recipeRequest, @RequestHeader("X-User-Email") String email) {
        return recipeService.create(recipeRequest, email);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeResponse> getAll() {
        return recipeService.getAll();
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeResponse getById(@PathVariable Long id) {
        return recipeService.getById(id);
    }

    @GetMapping("me")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeResponse> getAllForUserSelf(@RequestHeader("X-User-Email") String email) {
        return recipeService.getAllForUserSelf(email);
    }

    @PutMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public RecipeResponse update(@PathVariable Long id, @RequestBody RecipeRequest recipeRequest, @RequestHeader("X-User-Email") String email) {
        return recipeService.update(id, recipeRequest, email);
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

    @ExceptionHandler(DelimiterInInstructionException.class)
    public ResponseEntity<String> handleDelimiterInInstructionException(DelimiterInInstructionException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
