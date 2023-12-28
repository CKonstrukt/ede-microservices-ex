package application.services.service;

import application.services.dto.IngredientRequest;
import application.services.dto.IngredientResponse;
import application.services.exception.ResourceNotFoundException;
import application.services.model.Ingredient;
import application.services.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class IngredientService {
    private final IngredientRepository ingredientRepository;

    public void create(IngredientRequest ingredientRequest) {
        Ingredient ingredient = Ingredient.builder()
                .name(ingredientRequest.getName())
                .units(ingredientRequest.getUnits())
                .build();

        ingredientRepository.save(ingredient);
    }

    public List<IngredientResponse> getAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();

        return ingredients.stream().map(this::mapToIngredientResponse).toList();
    }

    public IngredientResponse getById(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Ingredient not found with id: " + id)
        );

        return mapToIngredientResponse(ingredient);
    }

    public IngredientResponse update(String id, IngredientRequest ingredientRequest) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Ingredient not found with id: " + id)
        );

        ingredient.setName(ingredientRequest.getName());
        ingredient.setUnits(ingredientRequest.getUnits());

        ingredientRepository.save(ingredient);

        return mapToIngredientResponse(ingredient);
    }

    public void delete(String id) {
        Ingredient ingredient = ingredientRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Ingredient not found with id: " + id)
        );

        ingredientRepository.delete(ingredient);
    }

    private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .units(ingredient.getUnits())
                .build();
    }
}
