package application.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RecipeRequest {
    private String userName;
    private List<RecipeIngredientRequest> recipeIngredients;
}
