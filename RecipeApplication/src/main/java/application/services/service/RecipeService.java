package application.services.service;

import application.services.dto.*;
import application.services.exception.ResourceNotFoundException;
import application.services.model.Recipe;
import application.services.model.RecipeIngredient;
import application.services.repository.RecipeRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final WebClient webClient;

    @Value("${userservice.baseurl}")
    private String userServiceBaseUrl;

    @Value("${ingredientservice.baseurl}")
    private String ingredientServiceBaseUrl;

    public UserResponse getUser(String id) {
        return webClient.get()
                .uri(String.format("http://%s/api/user/" + id, userServiceBaseUrl))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {throw new ResourceNotFoundException("User not found with id: " + id);})
                .bodyToMono(UserResponse.class)
                .block();
    }

    public IngredientResponse getIngredient(String id) {
        return webClient.get()
                .uri(String.format("http://%s/api/ingredient/" + id, ingredientServiceBaseUrl))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {throw new ResourceNotFoundException("Ingredient not found with id: " + id);})
                .bodyToMono(IngredientResponse.class)
                .block();
    }

    private RecipeIngredient mapToRecipeIngredient(RecipeIngredientRequest recipeIngredientRequest) {
        getIngredient(recipeIngredientRequest.getIngredientId());

        return RecipeIngredient.builder()
                .ingredientId(recipeIngredientRequest.getIngredientId())
                .quantity(recipeIngredientRequest.getQuantity())
                .unit(recipeIngredientRequest.getUnit())
                .build();
    }

    private RecipeIngredientResponse mapToRecipeIngredientResponse(RecipeIngredient recipeIngredient) {
        IngredientResponse ingredientResponse = getIngredient(recipeIngredient.getIngredientId());

        return RecipeIngredientResponse.builder()
                .name(ingredientResponse.getName())
                .quantity(recipeIngredient.getQuantity())
                .unit(recipeIngredient.getUnit())
                .build();
    }

    private RecipeResponse mapToRecipeResponse(Recipe recipe, UserResponse author) {
        List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients()
                .stream()
                .map(this::mapToRecipeIngredientResponse)
                .toList();

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .duration(recipe.getDuration())
                .description(recipe.getDescription())
                .instruction(recipe.getInstruction())
                .author(author.getFirstName() + " " + author.getLastName())
                .ingredients(ingredientResponses)
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }

    public RecipeResponse create(RecipeRequest recipeRequest) {
        UserResponse author = getUser(recipeRequest.getUserId());

        Recipe recipe = Recipe.builder()
                .name(recipeRequest.getName())
                .duration(recipeRequest.getDuration())
                .description(recipeRequest.getDescription())
                .instruction(recipeRequest.getInstruction())
                .userId(author.getId())
                .build();

        List<RecipeIngredient> recipeIngredients = recipeRequest.getIngredients().stream()
                .map(this::mapToRecipeIngredient)
                .toList();

        recipe.setIngredients(recipeIngredients);

        recipeRepository.save(recipe);

        return mapToRecipeResponse(recipe, author);
    }

    public RecipeResponse get(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + id)
        );

        UserResponse author = getUser(recipe.getUserId());

        return mapToRecipeResponse(recipe, author);
    }

    public List<RecipeResponse> getAllForUser(String userId) {
        UserResponse author = getUser(userId);

        return recipeRepository.findAllByUserIdEquals(userId).stream()
                .map(recipe -> mapToRecipeResponse(recipe, author))
                .toList();
    }

    public RecipeResponse update(Long id, RecipeRequest recipeRequest) {
        UserResponse author = getUser(recipeRequest.getUserId());

        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + id)
        );

        recipe.setName(recipeRequest.getName());
        recipe.setDuration(recipeRequest.getDuration());
        recipe.setDescription(recipeRequest.getDescription());

        List<RecipeIngredient> recipeIngredients = recipeRequest.getIngredients().stream()
                .map(this::mapToRecipeIngredient)
                .toList();

        recipe.getIngredients().clear();
        recipe.getIngredients().addAll(recipeIngredients);
        recipeRepository.save(recipe);

        recipe.setUpdatedAt(LocalDateTime.now()); // The save doesn't return updated time for some reason
        return mapToRecipeResponse(recipe, author);
    }

    public void delete(Long id) {
        try {
            recipeRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new ResourceNotFoundException("Recipe not found with id: " + id);
        }
    }
}
