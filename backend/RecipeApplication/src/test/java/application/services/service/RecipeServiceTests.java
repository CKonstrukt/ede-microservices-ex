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
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .description("description")
                .instruction("instruction")
                .userId("userId")
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenThrow(new ResourceNotFoundException("User not found with id: " + recipeRequest.getUserId()));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.create(recipeRequest);
        });
    }

    @Test
    public void create_WithValidRequest_SavesRecipe() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .description("description")
                .instruction("instruction")
                .userId("userId")
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class)).thenReturn(Mono.just(ingredientResponse1));

        // Act
        recipeService.create(recipeRequest);

        // Assert
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
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        Recipe recipe = Recipe.builder()
                .name("name")
                .duration(Duration.parse("PT1H"))
                .description("description")
                .instruction("instruction")
                .userId("authorId")
                .ingredients(List.of(recipeIngredient1))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        when(recipeRepository.findById(1L)).thenReturn(java.util.Optional.of(recipe));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class))
                .thenReturn(Mono.just(ingredientResponse1));

        // Act
        RecipeResponse recipeResponse = recipeService.getById(1L);

        // Assert
        assertEquals("name", recipeResponse.getName());
        assertEquals("PT1H", recipeResponse.getDuration().toString());
        assertEquals("description", recipeResponse.getDescription());
        assertEquals("instruction", recipeResponse.getInstruction());
        assertEquals("firstName lastName", recipeResponse.getAuthor());
        assertEquals(1, recipeResponse.getIngredients().size());
        assertEquals("ingredient1", recipeResponse.getIngredients().get(0).getName());
        assertEquals(1., recipeResponse.getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponse.getIngredients().get(0).getUnit());

        verify(recipeRepository, times(1)).findById(1L);
    }

    @Test
    public void getAllForUser_WithNonExistentUser_ThrowsResourceNotFoundException() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenThrow(new ResourceNotFoundException("User not found with id: authorId"));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.getAllForUser("authorId");
        });
    }

    @Test
    public void getAllForUser_WithValidRequest_ReturnsRecipes() {
        // Arrange
        RecipeIngredient recipeIngredient1 = RecipeIngredient.builder()
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        Recipe recipe1 = Recipe.builder()
                .name("name1")
                .duration(Duration.parse("PT1H"))
                .description("description1")
                .instruction("instruction1")
                .userId("authorId")
                .ingredients(List.of(recipeIngredient1))
                .build();

        UserResponse userResponse = UserResponse.builder()
                .email("email")
                .firstName("firstName")
                .lastName("lastName")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
                .name("ingredient1")
                .units(List.of("unit1"))
                .build();

        when(recipeRepository.findAllByUserIdEquals("authorId")).thenReturn(List.of(recipe1));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class))
                .thenReturn(Mono.just(ingredientResponse1));

        // Act
        List<RecipeResponse> recipeResponses = recipeService.getAllForUser("authorId");

        // Assert
        assertEquals(1, recipeResponses.size());
        assertEquals("name1", recipeResponses.get(0).getName());
        assertEquals("PT1H", recipeResponses.get(0).getDuration().toString());
        assertEquals("description1", recipeResponses.get(0).getDescription());
        assertEquals("instruction1", recipeResponses.get(0).getInstruction());
        assertEquals("firstName lastName", recipeResponses.get(0).getAuthor());
        assertEquals(1, recipeResponses.get(0).getIngredients().size());
        assertEquals("ingredient1", recipeResponses.get(0).getIngredients().get(0).getName());
        assertEquals(1., recipeResponses.get(0).getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponses.get(0).getIngredients().get(0).getUnit());

        verify(recipeRepository, times(1)).findAllByUserIdEquals("authorId");
    }

    @Test
    public void update_WithNonExistentUser_ThrowsResourceNotFoundException() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .description("description")
                .instruction("instruction")
                .userId("userId")
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any()))
                .thenThrow(new ResourceNotFoundException("User not found with id: " + recipeRequest.getUserId()));

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            recipeService.update(1L, recipeRequest);
        });
    }

    @Test
    public void update_WithValidRequest_UpdatesRecipe() {
        // Arrange
        RecipeIngredientRequest recipeIngredientRequest1 = RecipeIngredientRequest.builder()
                .ingredientId("ingredient1")
                .quantity(1.)
                .unit("unit1")
                .build();

        IngredientResponse ingredientResponse1 = IngredientResponse.builder()
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
                .firstName("firstName")
                .lastName("lastName")
                .build();

        Duration duration = Duration.parse("PT1H");
        RecipeRequest recipeRequest = RecipeRequest.builder()
                .name("name")
                .duration(duration)
                .description("description_test")
                .instruction("instruction_test")
                .userId("userId")
                .ingredients(List.of(recipeIngredientRequest1))
                .build();

        Recipe recipe = Recipe.builder()
                .name("name")
                .duration(duration)
                .description("description")
                .instruction("instruction")
                .userId("userId")
                .ingredients(List.of(recipeIngredient1))
                .build();

        when(recipeRepository.findById(1L)).thenReturn(java.util.Optional.of(recipe));
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(UserResponse.class)).thenReturn(Mono.just(userResponse));
        when(responseSpec.bodyToMono(IngredientResponse.class)).thenReturn(Mono.just(ingredientResponse1));

        // Act
        RecipeResponse recipeResponse = recipeService.update(1L, recipeRequest);

        // Assert
        assertEquals("name", recipeResponse.getName());
        assertEquals("PT1H", recipeResponse.getDuration().toString());
        assertEquals("description_test", recipeResponse.getDescription());
        assertEquals("instruction_test", recipeResponse.getInstruction());
        assertEquals("firstName lastName", recipeResponse.getAuthor());
        assertEquals(1, recipeResponse.getIngredients().size());
        assertEquals("ingredient1", recipeResponse.getIngredients().get(0).getName());
        assertEquals(1., recipeResponse.getIngredients().get(0).getQuantity());
        assertEquals("unit1", recipeResponse.getIngredients().get(0).getUnit());

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
                .name("name")
                .duration(Duration.parse("PT1H"))
                .description("description")
                .instruction("instruction")
                .userId("authorId")
                .ingredients(List.of(recipeIngredient1))
                .build();

        when(recipeRepository.findById(1L)).thenReturn(java.util.Optional.of(recipe));

        // Act
        recipeService.delete(1L);

        // Assert
        verify(recipeRepository, times(1)).findById(1L);
        verify(recipeRepository, times(1)).delete(any(Recipe.class));
    }
}
