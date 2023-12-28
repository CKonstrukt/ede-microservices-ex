package application.services.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecipeIngredientResponse {
    private String name;
    private Double quantity;
    private String unit;
}
