package application.services.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecipeIngredientRequest {
    private String ingredientId;
    private Double quantity;
    private String unit;
}
