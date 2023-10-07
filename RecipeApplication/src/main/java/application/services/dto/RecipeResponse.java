package application.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RecipeResponse {
    private String recipeNumber;
    private String userName;
    private List<RecipeIngredientResponse> recipeIngredients;
}
