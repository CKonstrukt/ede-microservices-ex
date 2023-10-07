package application.services.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "recipe")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Recipe {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String recipeNumber;
    private String userName;
    @OneToMany(cascade = CascadeType.ALL)
    private List<RecipeIngredient> recipeIngredients;
}
