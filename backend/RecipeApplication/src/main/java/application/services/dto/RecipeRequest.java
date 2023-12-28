package application.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RecipeRequest {
    private String name;
    private Duration duration;
    private String description;
    private String instruction;
    private String userId; // author of the recipe
    private List<RecipeIngredientRequest> ingredients;
}
