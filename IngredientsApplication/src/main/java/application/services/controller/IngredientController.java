package application.services.controller;

import application.services.dto.IngredientRequest;
import application.services.dto.IngredientResponse;
import application.services.exception.ResourceNotFoundException;
import application.services.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void create(@RequestBody IngredientRequest ingredientRequest) {
        ingredientService.create(ingredientRequest);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IngredientResponse getByCode(@PathVariable String id) {
        return ingredientService.getById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientResponse> getAll() {
        return ingredientService.getAll();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public IngredientResponse update(@PathVariable String id, @RequestBody IngredientRequest ingredientRequest) {
        return ingredientService.update(id, ingredientRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable String id) {
        ingredientService.delete(id);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }
}
