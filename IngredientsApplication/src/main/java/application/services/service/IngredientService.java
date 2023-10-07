package application.services.service;

import application.services.dto.IngredientRequest;
import application.services.dto.IngredientResponse;
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
                .code(ingredientRequest.getCode())
                .name(ingredientRequest.getName())
                .calories(ingredientRequest.getCalories())
                .build();

        ingredientRepository.save(ingredient);
    }

    public IngredientResponse getByCode(String code) {
        Ingredient ingredient = ingredientRepository.findByCode(code);

        return mapToIngredientResponse(ingredient);
    }

    public List<IngredientResponse> findAllByNameContains(String name) {
        List<Ingredient> ingredients = ingredientRepository.findAllByNameContainsIgnoreCase(name.toLowerCase());

        return ingredients.stream().map(this::mapToIngredientResponse).toList();
    }

    public List<IngredientResponse> getAll() {
        List<Ingredient> ingredients = ingredientRepository.findAll();

        return ingredients.stream().map(this::mapToIngredientResponse).toList();
    }

    public void delete(String id) {
        ingredientRepository.deleteById(id);
    }

    private IngredientResponse mapToIngredientResponse(Ingredient ingredient) {
        return IngredientResponse.builder()
                .id(ingredient.getId())
                .code(ingredient.getCode())
                .name(ingredient.getName())
                .calories(ingredient.getCalories())
                .build();
    }
}
