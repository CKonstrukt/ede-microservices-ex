package application.services.controller;

import application.services.dto.RecipeRequest;
import application.services.dto.RecipeResponse;
import application.services.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/recipe")
@RequiredArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createRecipe(@RequestBody RecipeRequest recipeRequest) {
        recipeService.create(recipeRequest);
    }

    @GetMapping("/all")
    @ResponseStatus(HttpStatus.OK)
    public List<RecipeResponse> getAllRecipes(@RequestParam String userName) {
        return recipeService.getAll(userName);
    }
}
