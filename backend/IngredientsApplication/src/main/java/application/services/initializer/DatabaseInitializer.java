package application.services.initializer;

import application.services.repository.IngredientRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseInitializer {
    @Bean
    CommandLineRunner initDatabase(IngredientRepository ingredientRepository) {
        return args -> {
            if (ingredientRepository.count() == 0) {
                ingredientRepository.saveAll(
                        IngredientDataInitializer.getIngredients()
                );
            }
        };
    }
}
