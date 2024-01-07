package application.services.service;

import application.services.dto.*;
import application.services.exception.ResourceNotFoundException;
import application.services.model.Recipe;
import application.services.model.RecipeIngredient;
import application.services.repository.RecipeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTests {
    @InjectMocks
    private RecipeService recipeService;

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private WebClient webClient;

    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private WebClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private WebClient.ResponseSpec responseSpec;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(recipeService, "userServiceBaseUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(recipeService, "ingredientServiceBaseUrl", "http://localhost:8082");
    }

    @Test
    public void createRecipe_WithNonExistentUser_ThrowsResourceNotFoundException() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .id("ingredient1Id")
                .quantity(1.)
                .unit("unit1")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .amountOfPeople(1)
                .description("description")
                .instructions(List.of("instruction1", "instruction2"))
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenThrow(new ResourceNotFoundException("User not found with email: " + "email"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.create(recipeRequest, "email");
        });
    }

    @Test
    public void create_WithValidRequest_SavesRecipe() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .id("ingredient1Id")
                .quantity(1.)
                .unit("unit1")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .id("ingredient1Id")
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("email")
                .name("name")
                .image("image")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .amountOfPeople(1)
                .description("description")
                .instructions(List.of("instruction1", "instruction2"))
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class)).thenReturn(Mono.just(ingredientResponse1));

        // Act
        RecipeResponse recipeResponse = recipeService.create(recipeRequest, userResponse.getEmail());

        // Assert
        assertEquals("name", recipeResponse.getName());
        assertEquals("PT1H", recipeResponse.getDuration().toString());
        assertEquals(1, recipeResponse.getAmountOfPeople());
        assertEquals("description", recipeResponse.getDescription());
        assertEquals(2, recipeResponse.getInstructions().size());
        assertEquals("instruction1", recipeResponse.getInstructions().get(0));
        assertEquals("instruction2", recipeResponse.getInstructions().get(1));
        assertEquals(1, recipeResponse.getIngredients().size());
        assertEquals("ingredient1Id", recipeResponse.getIngredients().get(0).getId());
        assertEquals("ingredient1", recipeResponse.getIngredients().get(0).getName());
        assertEquals(1., recipeResponse.getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponse.getIngredients().get(0).getUnit());
        assertEquals("name", recipeResponse.getUser().getName());
        assertEquals("image", recipeResponse.getUser().getImage());

        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    public void getById_WithNonExistentId_ThrowsResourceNotFoundException() {
        when(recipeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> recipeService.getById(1L));
        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void getById_WithValidRequest_ReturnsRecipe() {
        // Arrange
        RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
                .ingredientId("ingredient1Id")
                .quantity(1.)
                .unit("unit1")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .id("ingredient1Id")
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id("userId")
                .email("email")
                .name("name")
                .image("image")
                .build();

        Recipe recipe = Recipe.builder()
                .id(1L)
                .name("name")
                .duration(Duration.parse("PT1H"))
                .amountOfPeople(1)
                .description("description")
                .instructions("instruction1___instruction2")
                .ingredients(List.of(recipeIngredient1))
                .userId("userId")
                .build();

        when(recipeRepository.findById(recipe.getId())).thenReturn(java.util.Optional.of(recipe));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class))
                .thenReturn(Mono.just(ingredientResponse1));

        // Act
        RecipeResponse recipeResponse = recipeService.getById(recipe.getId());

        // Assert
        assertEquals(1L, recipeResponse.getId());
        assertEquals("name", recipeResponse.getName());
        assertEquals("PT1H", recipeResponse.getDuration().toString());
        assertEquals(1, recipeResponse.getAmountOfPeople());
        assertEquals("description", recipeResponse.getDescription());
        assertEquals(2, recipeResponse.getInstructions().size());
        assertEquals("instruction1", recipeResponse.getInstructions().get(0));
        assertEquals("instruction2", recipeResponse.getInstructions().get(1));
        assertEquals(1, recipeResponse.getIngredients().size());
        assertEquals("ingredient1Id", recipeResponse.getIngredients().get(0).getId());
        assertEquals("ingredient1", recipeResponse.getIngredients().get(0).getName());
        assertEquals(1., recipeResponse.getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponse.getIngredients().get(0).getUnit());
        assertEquals("name", recipeResponse.getUser().getName());
        assertEquals("image", recipeResponse.getUser().getImage());

        verify(recipeRepository, times(1)).findById(recipe.getId());
    }

    @Test
    public void getAllForUserSelf_WithNonExistentUser_ThrowsResourceNotFoundException() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new ResourceNotFoundException("User not found with email: email"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.getAllForUserSelf("email");
        });
    }

    @Test
    public void getAllForUserSelf_WithValidRequest_ReturnsRecipes() {
        // Arrange
        RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
                .ingredientId("ingredient1Id")
                .quantity(1.)
                .unit("unit1")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .id("ingredient1Id")
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id("userId")
                .email("email")
                .name("name")
                .image("image")
                .build();

        Recipe recipe1 = Recipe.builder()
                .id(1L)
                .name("name1")
                .duration(Duration.parse("PT1H"))
                .amountOfPeople(1)
                .description("description1")
                .instructions("instruction1___instruction2")
                .ingredients(List.of(recipeIngredient1))
                .userId("userId")
                .build();

        when(recipeRepository.findAllByUserIdEquals(userResponse.getId())).thenReturn(List.of(recipe1));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class))
                .thenReturn(Mono.just(ingredientResponse1));

        // Act
        List<RecipeResponse> recipeResponses = recipeService.getAllForUserSelf(userResponse.getEmail());

        // Assert
        assertEquals(1, recipeResponses.size());
        assertEquals(1L, recipeResponses.get(0).getId());
        assertEquals("name1", recipeResponses.get(0).getName());
        assertEquals("PT1H", recipeResponses.get(0).getDuration().toString());
        assertEquals(1, recipeResponses.get(0).getAmountOfPeople());
        assertEquals("description1", recipeResponses.get(0).getDescription());
        assertEquals(2, recipeResponses.get(0).getInstructions().size());
        assertEquals("instruction1", recipeResponses.get(0).getInstructions().get(0));
        assertEquals("instruction2", recipeResponses.get(0).getInstructions().get(1));
        assertEquals(1, recipeResponses.get(0).getIngredients().size());
        assertEquals("ingredient1", recipeResponses.get(0).getIngredients().get(0).getName());
        assertEquals(1., recipeResponses.get(0).getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponses.get(0).getIngredients().get(0).getUnit());
        assertEquals("name", recipeResponses.get(0).getUser().getName());
        assertEquals("image", recipeResponses.get(0).getUser().getImage());

        verify(recipeRepository, times(1)).findAllByUserIdEquals(userResponse.getId());
    }

    @Test
    public void update_WithNonExistentUser_ThrowsResourceNotFoundException() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .id("ingredient1Id")
                .quantity(1.)
                .unit("unit1")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .amountOfPeople(1)
                .description("description")
                .instructions(List.of("instruction1", "instruction2"))
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenThrow(new ResourceNotFoundException("User not found with email: email"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.update(1L, recipeRequest, "email");
        });
    }

    @Test
    public void update_WithValidRequest_UpdatesRecipe() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .id("ingredient1Id")
                .quantity(1.)
                .unit("unit1")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .id("ingredient1Id")
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("email")
                .name("name")
                .image("image")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name_1")
                .duration(duration)
                .amountOfPeople(2)
                .description("description_test")
                .instructions(List.of("instruction1", "instruction2", "instruction3"))
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        Recipe recipe = Recipe.builder()
                .id(1L)
                .name("name")
                .duration(duration)
                .description("description")
                .instructions("instruction1___instruction2")
                .ingredients(List.of(recipeIngredient1))
                .userId("userId")
                .build();

        when(recipeRepository.findById(recipe.getId())).thenReturn(java.util.Optional.of(recipe));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class)).thenReturn(Mono.just(ingredientResponse1));

        // Act
        RecipeResponse recipeResponse = recipeService.update(recipe.getId(), recipeRequest, userResponse.getEmail());

        // Assert
        assertEquals(1L, recipeResponse.getId());
        assertEquals("name_1", recipeResponse.getName());
        assertEquals("PT1H", recipeResponse.getDuration().toString());
        assertEquals(2, recipeResponse.getAmountOfPeople());
        assertEquals("description_test", recipeResponse.getDescription());
        assertEquals(3, recipeResponse.getInstructions().size());
        assertEquals("instruction1", recipeResponse.getInstructions().get(0));
        assertEquals("instruction2", recipeResponse.getInstructions().get(1));
        assertEquals("instruction3", recipeResponse.getInstructions().get(2));
        assertEquals(1, recipeResponse.getIngredients().size());
        assertEquals("ingredient1", recipeResponse.getIngredients().get(0).getName());
        assertEquals(1., recipeResponse.getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponse.getIngredients().get(0).getUnit());
        assertEquals("name", recipeResponse.getUser().getName());
        assertEquals("image", recipeResponse.getUser().getImage());

        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, times(1)).save(any(Recipe.class));
    }

    @Test
    public void delete_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(recipeRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.delete(1L);
        });

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void delete_WithValidRequest_DeletesRecipe() {
        // Arrange
        RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        Recipe recipe = Recipe.builder()
                .id(1L)
                .name("name")
                .duration(Duration.parse("PT1H"))
                .amountOfPeople(1)
                .description("description")
                .instructions("instruction1___instruction2")
                .ingredients(List.of(recipeIngredient1))
                .userId("userId")
                .build();

        when(recipeRepository.findById(recipe.getId())).thenReturn(java.util.Optional.of(recipe));

        // Act
        recipeService.delete(recipe.getId());

        // Assert
        verify(recipeRepository, times(1)).findById(recipe.getId());
        verify(recipeRepository, times(1)).delete(any(Recipe.class));
    }
}
