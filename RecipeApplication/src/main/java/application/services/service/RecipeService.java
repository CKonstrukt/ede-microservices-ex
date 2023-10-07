package application.services.service;

import application.services.dto.*;
import application.services.model.Recipe;
import application.services.model.RecipeIngredient;
import application.services.repository.RecipeRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final WebClient webClient;

    public void create(RecipeRequest recipeRequest) {
        Recipe recipe = Recipe.builder()
                .recipeNumber(UUID.randomUUID().toString())
                .userName(recipeRequest.getUserName())
                .build();

        List<RecipeIngredient> recipeIngredients = recipeRequest.getRecipeIngredients().stream()
                .map(this::mapToRecipeIngredient)
                .toList();

        recipe.setRecipeIngredients(recipeIngredients);
        System.out.println(recipe.getRecipeIngredients().get(0).getCode());

        recipeRepository.save(recipe);
    }

    public List<RecipeResponse> getAll(String userName) {
        return recipeRepository.findAllByUserNameEquals(userName).stream()
                .map(this::mapToRecipeResponse)
                .toList();
    }

    private RecipeIngredient mapToRecipeIngredient(RecipeIngredientRequest recipeIngredientRequest) {
        return RecipeIngredient.builder()
                .code(recipeIngredientRequest.getCode())
                .quantity(recipeIngredientRequest.getQuantity())
                .unit(recipeIngredientRequest.getUnit())
                .build();
    }

    private RecipeIngredientResponse mapToRecipeIngredientResponse(RecipeIngredient recipeIngredient) {
        System.out.println(recipeIngredient.getCode());
        IngredientResponse ingredientResponse = webClient.get()
                .uri("http://localhost:8082/api/ingredient", uriBuilder -> uriBuilder.queryParam("code", recipeIngredient.getCode()).build())
                .retrieve()
                .bodyToMono(IngredientResponse.class)
                .block();

        assert ingredientResponse != null;
        return RecipeIngredientResponse.builder()
                .code(ingredientResponse.getCode())
                .name(ingredientResponse.getName())
                .totalCalories(ingredientResponse.getCalories() * recipeIngredient.getQuantity())
                .quantity(recipeIngredient.getQuantity())
                .unit(recipeIngredient.getUnit())
                .build();
    }

    private RecipeResponse mapToRecipeResponse(Recipe recipe) {
        List<RecipeIngredientResponse> ingredientResponses = recipe.getRecipeIngredients()
                .stream()
                .map(this::mapToRecipeIngredientResponse)
                .toList();

        return RecipeResponse.builder()
                .recipeNumber(recipe.getRecipeNumber())
                .userName(recipe.getUserName())
                .recipeIngredients(ingredientResponses)
                .build();
    }
}
