package application.services.initializer;

import application.services.model.Ingredient;

import java.util.List;

public class IngredientDataInitializer {
    public static List<Ingredient> getIngredients() {
        return List.of(
                Ingredient.builder()
                        .name("Flour")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Sugar")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Egg")
                        .units(List.of("pcs"))
                        .build(),
                Ingredient.builder()
                        .name("Milk")
                        .units(List.of("ml", "l"))
                        .build(),
                Ingredient.builder()
                        .name("Butter")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Salt")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Baking powder")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Vanilla")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Cocoa")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Chocolate")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Cream")
                        .units(List.of("ml", "l"))
                        .build(),
                Ingredient.builder()
                        .name("Fruit")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Nuts")
                        .units(List.of("g", "kg", "pcs"))
                        .build(),
                Ingredient.builder()
                        .name("Cinnamon")
                        .units(List.of("g", "kg", "pcs"))
                        .build(),
                Ingredient.builder()
                        .name("Honey")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Jam")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Yogurt")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Sour cream")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Cottage cheese")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Ricotta")
                        .units(List.of("g", "kg"))
                        .build(),
                Ingredient.builder()
                        .name("Tomato")
                        .units(List.of("g", "kg", "pcs"))
                        .build(),
                Ingredient.builder()
                        .name("Cucumber")
                        .units(List.of("g", "kg", "pcs"))
                        .build(),
                Ingredient.builder()
                        .name("Spaghetti")
                        .units(List.of("g", "kg"))
                        .build());
    }

}
