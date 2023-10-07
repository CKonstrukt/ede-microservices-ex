package application.services.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "ingredient")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Ingredient {
    private String id;
    private String code;
    private String name;
    private Double calories;
}
