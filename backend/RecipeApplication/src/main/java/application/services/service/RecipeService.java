package application.services.service;

import application.services.dto.*;
import application.services.exception.DelimiterInInstructionException;
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
import java.util.ArrayList;
import java.util.Collections;
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

    private final String INSTRUCTION_DELIMITER = "___";

    private UserResponse getUserById(String id) {
        return webClient.get()
                .uri(String.format("http://%s/api/user/" + id, userServiceBaseUrl))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {throw new ResourceNotFoundException("User not found with id: " + id);})
                .bodyToMono(UserResponse.class)
                .block();
    }

    private UserResponse getUserByEmail(String email) {
        return webClient.get()
                .uri(String.format("http://%s/api/user/email/" + email, userServiceBaseUrl))
                .retrieve()
                .onStatus(
                        HttpStatusCode::is4xxClientError,
                        clientResponse -> {throw new ResourceNotFoundException("User not found with email: " + email);})
                .bodyToMono(UserResponse.class)
                .block();
    }

    private IngredientResponse getIngredient(String id) {
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
        getIngredient(recipeIngredientRequest.getId());

        return RecipeIngredient.builder()
                .ingredientId(recipeIngredientRequest.getId())
                .quantity(recipeIngredientRequest.getQuantity())
                .unit(recipeIngredientRequest.getUnit())
                .build();
    }

    private RecipeIngredientResponse mapToRecipeIngredientResponse(RecipeIngredient recipeIngredient) {
        IngredientResponse ingredientResponse = getIngredient(recipeIngredient.getIngredientId());

        return RecipeIngredientResponse.builder()
                .id(ingredientResponse.getId())
                .name(ingredientResponse.getName())
                .quantity(recipeIngredient.getQuantity())
                .unit(recipeIngredient.getUnit())
                .build();
    }

    private RecipeResponse mapToRecipeResponse(Recipe recipe, UserResponse user) {
        List<RecipeIngredientResponse> ingredientResponses = recipe.getIngredients()
                .stream()
                .map(this::mapToRecipeIngredientResponse)
                .toList();

        RecipeUserResponse recipeUserResponse = RecipeUserResponse.builder()
                .name(user.getName())
                .image(user.getImage())
                .build();

        List<String> instructions = splitInstruction(recipe.getInstructions());

        return RecipeResponse.builder()
                .id(recipe.getId())
                .name(recipe.getName())
                .duration(recipe.getDuration())
                .amountOfPeople(recipe.getAmountOfPeople())
                .description(recipe.getDescription())
                .instructions(instructions)
                .ingredients(ingredientResponses)
                .user(recipeUserResponse)
                .createdAt(recipe.getCreatedAt())
                .updatedAt(recipe.getUpdatedAt())
                .build();
    }

    private String concatInstruction(List<String> instructions) throws DelimiterInInstructionException {
        //TODO: Sanitize instructions

        boolean containsDelimiter = instructions.stream()
                .anyMatch(instruction -> instruction.contains(INSTRUCTION_DELIMITER));
        if (containsDelimiter) {
            throw new DelimiterInInstructionException("Instruction contains delimiter.");
        }

        return String.join(INSTRUCTION_DELIMITER, instructions);
    }

    private List<String> splitInstruction(String instruction) {
        return List.of(instruction.split(INSTRUCTION_DELIMITER));
    }

    public RecipeResponse create(RecipeRequest recipeRequest, String email) {
        UserResponse user = getUserByEmail(email);

        String instructions = concatInstruction(recipeRequest.getInstructions());

        Recipe recipe = Recipe.builder()
                .name(recipeRequest.getName())
                .duration(recipeRequest.getDuration())
                .amountOfPeople(recipeRequest.getAmountOfPeople())
                .description(recipeRequest.getDescription())
                .instructions(instructions)
                .userId(user.getId())
                .build();

        List<RecipeIngredient> recipeIngredients = recipeRequest.getIngredients().stream()
                .map(this::mapToRecipeIngredient)
                .toList();

        recipe.setIngredients(recipeIngredients);

        recipeRepository.save(recipe);

        return mapToRecipeResponse(recipe, user);
    }

    public List<RecipeResponse> getAll() {
        List<Recipe> recipes = recipeRepository.findAll();

        return recipes.stream()
                .map(recipe -> mapToRecipeResponse(recipe, getUserById(recipe.getUserId())))
                .toList();
    }

    public RecipeResponse getById(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + id)
        );

        UserResponse user = getUserById(recipe.getUserId());

        return mapToRecipeResponse(recipe, user);
    }

    public List<RecipeResponse> getAllForUserSelf(String email) {
        UserResponse user = getUserByEmail(email);

        return recipeRepository.findAllByUserIdEquals(user.getId()).stream()
                .map(recipe -> mapToRecipeResponse(recipe, user))
                .toList();
    }

    public RecipeResponse update(Long id, RecipeRequest recipeRequest, String email) {
        UserResponse user = getUserByEmail(email);

        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + id)
        );

        String instructions = concatInstruction(recipeRequest.getInstructions());

        recipe.setName(recipeRequest.getName());
        recipe.setDuration(recipeRequest.getDuration());
        recipe.setAmountOfPeople(recipeRequest.getAmountOfPeople());
        recipe.setDescription(recipeRequest.getDescription());
        recipe.setInstructions(instructions);

        List<RecipeIngredient> recipeIngredients = recipeRequest.getIngredients().stream()
                .map(this::mapToRecipeIngredient)
                .toList();

        recipe.setIngredients(new ArrayList<>(recipeIngredients));
        recipeRepository.save(recipe);

        recipe.setUpdatedAt(LocalDateTime.now()); // The save doesn't return updated time for some reason
        return mapToRecipeResponse(recipe, user);
    }

    public void delete(Long id) {
        Recipe recipe = recipeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Recipe not found with id: " + id)
        );

        recipeRepository.delete(recipe);
    }
}
