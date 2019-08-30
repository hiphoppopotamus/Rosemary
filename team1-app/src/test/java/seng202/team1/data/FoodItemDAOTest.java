package seng202.team1.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team1.model.FoodItem;
import seng202.team1.util.InvalidDataCodeException;
import seng202.team1.util.UnitType;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FoodItemDAOTest {

    private FoodItemDAO foodStorage;
    private FoodItem suppliedTestItem;

    @BeforeEach
    void setupStorage() {
        foodStorage = MemoryStorage.getInstance(); // TODO make this more modular??
        ((MemoryStorage) foodStorage).resetInstance(); // TODO this feels bad
        suppliedTestItem = new FoodItem("ITEM1", "Oil", UnitType.GRAM);
    }

    @Test
    void testGetByCode() {
        assertNull(foodStorage.getFoodItemByCode(suppliedTestItem.getCode()));

        foodStorage.addFoodItem(suppliedTestItem, 1);
        assertEquals(suppliedTestItem, foodStorage.getFoodItemByCode(suppliedTestItem.getCode()));

        assertNull(foodStorage.getFoodItemByCode("UNUSED CODE"));

        assertNull(foodStorage.getFoodItemByCode(null));
    }

    @Test
    void testGetAll() {
        Set<FoodItem> items = foodStorage.getAllFoodItems();
        assertEquals(0, items.size());

        foodStorage.addFoodItem(suppliedTestItem, 0);
        items = foodStorage.getAllFoodItems();
        assertEquals(1, items.size());
        assertTrue(items.contains(suppliedTestItem));
    }

    @Test
    void testAdd() {
        foodStorage.addFoodItem(suppliedTestItem, 1000);
        assertEquals(suppliedTestItem,
                foodStorage.getFoodItemByCode(suppliedTestItem.getCode()));

        // test adding an already added item
        assertThrows(InvalidDataCodeException.class, () -> {
            foodStorage.addFoodItem(suppliedTestItem, 1000);
        });

        // test adding null
        assertThrows(NullPointerException.class, () -> {
            foodStorage.addFoodItem(null, 1000);
        });
    }

    @Test
    void testEdit() {
        foodStorage.addFoodItem(suppliedTestItem, 1000);

        FoodItem alteredItem = new FoodItem("ITEM1", "Canola Oil", UnitType.GRAM);
        foodStorage.updateFoodItem(alteredItem);
        assertEquals(alteredItem, foodStorage.getFoodItemByCode(suppliedTestItem.getCode()));

        // the item's code does not exist within storage
        FoodItem notStoredItem = new FoodItem("ITEM2", "Eggs", UnitType.COUNT);
        assertThrows(InvalidDataCodeException.class, () -> {
            foodStorage.updateFoodItem(notStoredItem);
        });

        assertThrows(NullPointerException.class, () -> {
            foodStorage.updateFoodItem(null);
        });
    }

    @Test
    void testRemove() {
        foodStorage.addFoodItem(suppliedTestItem, 1000);

        foodStorage.removeFoodItem(suppliedTestItem.getCode());
        assertNull(foodStorage.getFoodItemByCode(suppliedTestItem.getCode()));

        assertThrows(InvalidDataCodeException.class, () -> {
            foodStorage.removeFoodItem(suppliedTestItem.getCode());
        });
    }

    @Test
    void testGetSetStock() {
        foodStorage.addFoodItem(suppliedTestItem, 1000);
        assertEquals(1000, foodStorage.getFoodItemStock(suppliedTestItem.getCode()));

        foodStorage.setFoodItemStock(suppliedTestItem.getCode(), 500);
        assertEquals(500, foodStorage.getFoodItemStock(suppliedTestItem.getCode()));

        foodStorage.setFoodItemStock(suppliedTestItem.getCode(), 0);
        assertEquals(0, foodStorage.getFoodItemStock(suppliedTestItem.getCode()));

        assertThrows(IllegalArgumentException.class, () -> {
            foodStorage.setFoodItemStock(suppliedTestItem.getCode(), -1);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            foodStorage.setFoodItemStock(suppliedTestItem.getCode(), Integer.MAX_VALUE + 1);
        });

        assertThrows(InvalidDataCodeException.class, () -> {
            foodStorage.setFoodItemStock("UNUSED CODE", 100);
        });

        assertThrows(InvalidDataCodeException.class, () -> {
            foodStorage.setFoodItemStock(null, 100);
        });
    }


}