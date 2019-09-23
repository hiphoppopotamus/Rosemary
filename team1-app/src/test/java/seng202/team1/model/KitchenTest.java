package seng202.team1.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import seng202.team1.data.DAOFactory;
import seng202.team1.data.FoodItemDAO;
import seng202.team1.data.MemoryStorage;
import seng202.team1.util.InvalidDataCodeException;
import seng202.team1.util.NotEnoughStockException;
import seng202.team1.util.RecipeNotFoundException;
import seng202.team1.util.UnitType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@Disabled
class KitchenTest {

    FoodItemDAO storage;
    Kitchen kitchen;

    @BeforeEach
    void beforeEach() {
        storage = MemoryStorage.getInstance();
        DAOFactory.resetInstances();
        kitchen = new Kitchen(storage);
    }

    @Test
    void testConstructor() {
        // null storage instance
        assertThrows(NullPointerException.class, () -> {
            new Kitchen(null);
        });
    }

    @Test
    void testCreateFoodItemsArgErrors() {
        // code is null
        assertThrows(NullPointerException.class, () -> {
            kitchen.createFoodItems(null, 1);
        });

        // amount is < 0
        assertThrows(IllegalArgumentException.class, () -> {
            kitchen.createFoodItems("12345", -1);
        });

        // amount = 0
        assertThrows(IllegalArgumentException.class, () -> {
            kitchen.createFoodItems("12345", 0);
        });

        // FoodItem doesn't exist
        assertThrows(InvalidDataCodeException.class, () -> {
            kitchen.createFoodItems("12345", 1);
        });

        // FoodItem exists but has no recipe
        fail("not yet implemented");

        // recipe exists but not enough stock in storage
        FoodItem testItem = new FoodItem("TESTITEM", "test food item", UnitType.COUNT);
        FoodItem testIngredient = new FoodItem("ING1", "ingred", UnitType.COUNT);
        Set<FoodItem> ingredients = new HashSet<>(Arrays.asList(testIngredient));
        Map<String, Integer> ingredAmounts = new HashMap<>();
        ingredAmounts.put(testIngredient.getCode(), 2);
        testItem.setRecipe(new Recipe(ingredients, new HashSet<>(), ingredAmounts, 1));
        storage.addFoodItem(testIngredient, 5);
        storage.addFoodItem(testItem, 0);
        assertThrows(NotEnoughStockException.class, () -> {
            kitchen.createFoodItems(testItem.getCode(), 10);
        });
    }

    @Test
    void testCreateFoodItemsAmounts() {
        // recipe exists enough stock in storage
        FoodItem foodItem = new FoodItem("TESTITEM2", "test item 2", UnitType.COUNT);
        Recipe recipe = new Recipe(null, null, null, 1);
        foodItem.setRecipe(recipe);
        List<FoodItem> expectedItems = Arrays.asList(foodItem);
        assertEquals(expectedItems, kitchen.createFoodItems("TESTITEM2", 1));

        // more than one
        List<FoodItem> expectedItems2 = Arrays.asList(foodItem, foodItem);
        assertEquals(expectedItems, kitchen.createFoodItems("TESTITEM2", 2));

        FoodItem gramFoodItem = new FoodItem("GRAMITEM", "item measured grams", UnitType.GRAM);
        Recipe recipeFor100 = new Recipe(null, null, null, 100);

        kitchen.createFoodItems("GRAMITEM", 100);
        fail("not yet implemented");

        kitchen.createFoodItems("GRAMITEM", 60);
        fail("not yet implemented");

        kitchen.createFoodItems("GRAMITEM", 120);
        fail("not yet implemented");

    }
}