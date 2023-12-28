package application.services.service;

import application.services.dto.IngredientRequest;
import application.services.dto.IngredientResponse;
import application.services.exception.ResourceNotFoundException;
import application.services.model.Ingredient;
import application.services.repository.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTests {
    @InjectMocks
    private IngredientService ingredientService;

    @Mock
    private IngredientRepository ingredientRepository;

    @Test
    public void create_WithValidRequest_SavesIngredient() {
        // Arrange
        IngredientRequest ingredientRequest = IngredientRequest.builder()
                .name("name")
                .units(List.of("unit1", "unit2"))
                .build();

        // Act
        ingredientService.create(ingredientRequest);

        // Assert
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    public void getAll_WithValidRequest_ReturnsIngredients() {
        // Arrange
        Ingredient ingredient1 = Ingredient.builder()
                .name("name1")
                .units(List.of("unit1.1", "unit1.2"))
                .build();

        Ingredient ingredient2 = Ingredient.builder()
                .name("name2")
                .units(List.of("unit2.1", "unit2.2"))
                .build();
        when(ingredientRepository.findAll()).thenReturn(List.of(ingredient1, ingredient2));

        // Act
        List<IngredientResponse> ingredients = ingredientService.getAll();

        // Assert
        assertEquals(2, ingredients.size());
        assertEquals("name1", ingredients.get(0).getName());
        assertEquals("name2", ingredients.get(1).getName());
        assertArrayEquals(new String[]{"unit1.1", "unit1.2"}, ingredients.get(0).getUnits().toArray());
        assertArrayEquals(new String[]{"unit2.1", "unit2.2"}, ingredients.get(1).getUnits().toArray());

        verify(ingredientRepository, times(1)).findAll();
    }

    @Test
    public void getById_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(ingredientRepository.findById("id")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredientService.getById("id"));

        verify(ingredientRepository, times(1)).findById("id");
    }

    @Test
    public void getById_WithValidRequest_ReturnsIngredient() {
        // Arrange
        Ingredient ingredient = Ingredient.builder()
                .name("name")
                .units(List.of("unit1", "unit2"))
                .build();
        when(ingredientRepository.findById("id")).thenReturn(java.util.Optional.of(ingredient));

        // Act
        IngredientResponse ingredientResponse = ingredientService.getById("id");

        // Assert
        assertEquals("name", ingredientResponse.getName());
        assertArrayEquals(new String[]{"unit1", "unit2"}, ingredientResponse.getUnits().toArray());

        verify(ingredientRepository, times(1)).findById("id");
    }

    @Test
    public void update_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(ingredientRepository.findById("id")).thenReturn(java.util.Optional.empty());

        IngredientRequest ingredientRequest = IngredientRequest.builder()
                .name("new name")
                .units(List.of("new unit1", "new unit2"))
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredientService.update("id", ingredientRequest));

        verify(ingredientRepository, times(1)).findById("id");
    }

    @Test
    public void update_WithValidRequest_UpdatesIngredient() {
        // Arrange
        Ingredient ingredient = Ingredient.builder()
                .name("name")
                .units(List.of("unit1", "unit2"))
                .build();
        when(ingredientRepository.findById("id")).thenReturn(java.util.Optional.of(ingredient));

        IngredientRequest ingredientRequest = IngredientRequest.builder()
                .name("new name")
                .units(List.of("new unit1", "new unit2"))
                .build();

        // Act
        IngredientResponse ingredientResponse = ingredientService.update("id", ingredientRequest);

        // Assert
        assertEquals("new name", ingredientResponse.getName());
        assertArrayEquals(new String[]{"new unit1", "new unit2"}, ingredientResponse.getUnits().toArray());

        verify(ingredientRepository, times(1)).findById("id");
        verify(ingredientRepository, times(1)).save(any(Ingredient.class));
    }

    @Test
    public void delete_WithNonExistentId_ThrowsResourceNotFoundException() {
        // Arrange
        when(ingredientRepository.findById("id")).thenReturn(java.util.Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ingredientService.delete("id"));

        verify(ingredientRepository, times(1)).findById("id");
    }

    @Test
    public void delete_WithValidRequest_DeletesIngredient(){
        // Arrange
        Ingredient ingredient = Ingredient.builder()
                .name("name")
                .units(List.of("unit1", "unit2"))
                .build();
        when(ingredientRepository.findById("id")).thenReturn(java.util.Optional.of(ingredient));

        // Act
        ingredientService.delete("id");

        // Assert
        verify(ingredientRepository, times(1)).findById("id");
        verify(ingredientRepository, times(1)).delete(ingredient);
    }
}
