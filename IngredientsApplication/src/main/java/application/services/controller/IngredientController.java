package application.services.controller;

import application.services.dto.IngredientRequest;
import application.services.dto.IngredientResponse;
import application.services.service.IngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ingredient")
@RequiredArgsConstructor
public class IngredientController {
    private final IngredientService ingredientService;

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public void create(@RequestBody IngredientRequest ingredientRequest) {
        System.out.println(ingredientRequest.getCalories());
        ingredientService.create(ingredientRequest);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public IngredientResponse getByCode(@RequestParam String code) {
        return ingredientService.getByCode(code);
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientResponse> findAllByNameContains(@RequestParam String name) {
        return ingredientService.findAllByNameContains(name);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<IngredientResponse> getAll() {
        return ingredientService.getAll();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.OK)
    public void delete(@RequestParam String id) {
        ingredientService.delete(id);
    }
}
