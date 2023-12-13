package application.services.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> favouriteRecipes;
}
