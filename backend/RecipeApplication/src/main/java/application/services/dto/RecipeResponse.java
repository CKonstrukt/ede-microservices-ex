package application.services.dto;

import application.services.model.RecipeIngredient;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RecipeResponse {
    private Long id;
    private String name;
    private Duration duration;
    private Integer amountOfPeople;
    private String description;
    private List<String> instructions;
    private List<RecipeIngredientResponse> ingredients;
    private RecipeUserResponse user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
