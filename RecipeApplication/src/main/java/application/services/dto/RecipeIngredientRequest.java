package application.services.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class RecipeIngredientRequest {
    private String code;
    private Integer quantity;
    private String unit;
}
