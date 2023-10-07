package application.services.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecipeIngredientResponse {
    private String code;
    private String name;
    private Double totalCalories;
    private Integer quantity;
    private String unit;
}
