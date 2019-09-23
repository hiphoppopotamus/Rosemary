package seng202.team1.model;

import org.joda.money.BigMoney;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import seng202.team1.util.UnitType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static seng202.team1.util.UnitType.COUNT;

class FoodItemTest {

    FoodItem testItem;

    @BeforeEach
    void beforeEach() {
        testItem = new FoodItem("TEST", "Test Item", COUNT);
    }

    @Test
    void testConstructor() {
        // how should we test abstract classes??
        FoodItem item1 = new FoodItem("COD", "1", COUNT);
        assertEquals("COD", item1.getCode());
        assertEquals("1", item1.getName());


        FoodItem item2 = new FoodItem("THISIS10", "thisstring is twenty", COUNT);
        assertEquals(item2.getName(), "thisstring is twenty");

        // code not enough chars
        assertThrows(IllegalArgumentException.class, () -> {
            new FoodItem("CC", "E", COUNT);
        });

        // code too many chars
        assertThrows(IllegalArgumentException.class, () -> {
            new FoodItem("elevenchars", "E", COUNT);
        });

        // code not uppercase alphanumeric
        assertThrows(IllegalArgumentException.class, () -> {
            new FoodItem("code", "Test Name", COUNT);
        });
        assertThrows(IllegalArgumentException.class, () -> {
            new FoodItem("COD\u2202", "Test Name", COUNT);
        });

        assertThrows(NullPointerException.class, () -> {
            new FoodItem(null, "Test Name", COUNT);
        });
    }

    @Test
    void testRecipe() {
        Set<FoodItem> ingredients = new HashSet<>();
        Set<FoodItem> addableIngredients = new HashSet<>();
        Map<String, Integer> ingredientAmounts = new HashMap<>();

        FoodItem ingredient1 = new FoodItem("INGRED1", "test ingredient 1", COUNT);
        ingredients.add(ingredient1);
        ingredientAmounts.put(ingredient1.getCode(), 1);
        Recipe testRecipe = new Recipe(ingredients, addableIngredients, ingredientAmounts, 1);

        // default recipe
        assertNull(testItem.getRecipe());

        // trying to remove the current recipe when no recipe is set
        testItem.setRecipe(null);
        assertNull(testItem.getRecipe());

        // setting a FoodItem's recipe
        testItem.setRecipe(testRecipe);
        assertEquals(testRecipe, testItem.getRecipe());

        FoodItem ingredient2 = new FoodItem("INGRED2", "test ingredient 2", COUNT);
        ingredients.add(ingredient2);
        ingredientAmounts.put(ingredient2.getCode(), 1);
        testRecipe = new Recipe(ingredients, addableIngredients, ingredientAmounts, 2);

        // overriding the existing recipe
        testItem.setRecipe(testRecipe);
        assertEquals(testRecipe, testItem.getRecipe());

        // removing the existing recipe
        testItem.setRecipe(null);
        assertNull(testItem.getRecipe());
    }

    @Test
    void testName() {
        testItem.setName("test rename");
        assertEquals("test rename", testItem.getName());

        testItem.setName("1");
        assertEquals("1", testItem.getName());

        testItem.setName("thisstring is twenty");
        assertEquals("thisstring is twenty", testItem.getName());

        assertThrows(IllegalArgumentException.class, () -> {
            testItem.setName("");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            testItem.setName("thisstringistwentyone");
        });

        assertThrows(NullPointerException.class, () -> {
            testItem.setName(null);
        });
    }

    @Test
    void testCaloriesPerUnit() {
        double cal = 0.45;
        testItem.setCaloriesPerUnit(cal);
        assertEquals(cal, testItem.getCaloriesPerUnit());

        double negCal = -0.1;
        assertThrows(IllegalArgumentException.class, () -> {
            testItem.setCaloriesPerUnit(negCal);
        });
    }

    @Test
    void testUnit() {
        testItem.setUnit(UnitType.GRAM);
        assertEquals(UnitType.GRAM, testItem.getUnit());

        testItem.setUnit(UnitType.ML);
        assertEquals(UnitType.ML, testItem.getUnit());

        testItem.setUnit(COUNT);
        assertEquals(COUNT, testItem.getUnit());

        assertThrows(NullPointerException.class, () -> {
            testItem.setUnit(null);
        });

        assertEquals("g", UnitType.GRAM.toString());
        assertEquals("m", UnitType.ML.toString());
        assertEquals("c", UnitType.COUNT.toString());
    }

    @Test
    void testIsVegetarian() {
        assertEquals(false, testItem.getIsVegetarian()); // default value

        testItem.setIsVegetarian(true);
        assertEquals(true, testItem.getIsVegetarian());
        assertEquals(false, testItem.getIsVegan()); // should not change

        testItem.setIsVegan(true);
        testItem.setIsVegetarian(false);
        assertEquals(false, testItem.getIsVegetarian());
        assertEquals(false, testItem.getIsVegan()); // should change from true to false

        testItem.setIsVegetarian(true);
        assertEquals(true, testItem.getIsVegetarian());
        assertEquals(false, testItem.getIsVegan()); // should not change
    }

    @Test
    void testIsVegan() {
        assertEquals(false, testItem.getIsVegan()); // default value
        assertEquals(false, testItem.getIsVegetarian()); // default value

        testItem.setIsVegan(true);
        assertEquals(true, testItem.getIsVegan());
        assertEquals(true, testItem.getIsVegetarian()); // should change from false to true

        testItem.setIsVegan(false);
        assertEquals(false, testItem.getIsVegan());
        assertEquals(true, testItem.getIsVegetarian()); // should not change
    }

    @Test
    void testIsGlutenFree() {
        assertEquals(false, testItem.getIsGlutenFree()); // default value

        testItem.setIsGlutenFree(true);
        assertEquals(true, testItem.getIsGlutenFree());

        testItem.setIsGlutenFree(false);
        assertEquals(false, testItem.getIsGlutenFree());
    }

    @Test
    void testCost() {
        // default value
        assertEquals(BigMoney.parse("NZD 0.00"), testItem.getCost());

        // null value
        assertThrows(NullPointerException.class, () -> {
            testItem.setCost(null);
        });

        // set cost
        testItem.setCost(BigMoney.parse("NZD 5.86"));
        assertEquals(BigMoney.parse("NZD 5.86"), testItem.getCost());

        // more than 2 d.p.
        testItem.setCost(BigMoney.parse("NZD 0.05439273"));
        assertEquals(BigMoney.parse("NZD 0.05439273"), testItem.getCost());

        // negative cost
        assertThrows(IllegalArgumentException.class, () -> {
            testItem.setCost(BigMoney.parse("NZD -0.5"));
        });
    }

    @Test void testToString() {
        assertEquals(testItem.toString(), ("FoodItem{" +
                "code='" + testItem.getCode() + '\'' +
                ", name='" + testItem.getName() + '\'' +
                ", unit=" + testItem.getUnit() +
                ", cost=" + testItem.getCost() +
                ", isVegetarian=" + testItem.getIsVegetarian() +
                ", isVegan=" + testItem.getIsVegan() +
                ", isGlutenFree=" + testItem.getIsGlutenFree() +
                ", caloriesPerUnit=" + testItem.getCaloriesPerUnit() +
                ", recipe=" + testItem.getRecipe() +
                '}'));

    }

    @Test
    void testEquals() {
        FoodItem item1 = new FoodItem("COD", "1", COUNT);
        FoodItem item2 = new FoodItem("COD", "1", COUNT);
        FoodItem item3 = new FoodItem("CAD", "2", COUNT);

        //check two identical items are considered the same
        assertEquals(item1, item2);

        //check two different items are not considered the same
        assertNotEquals(item1, item3);

    }

}