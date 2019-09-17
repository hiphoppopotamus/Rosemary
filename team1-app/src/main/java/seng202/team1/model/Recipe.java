package seng202.team1.model;

import java.util.*;

public class Recipe {

    private int amountCreated;
    private Map<String, Integer> ingredientAmounts = new HashMap<>();
    private Set<FoodItem> ingredients = new HashSet<>();
    private Set<FoodItem> addableIngredients = new HashSet<>();

    /**
     * Default constructor
     * @param ingredients the set of food items
     * @param addableIngredients the set of food items that can be added to a recipe during an order
     */
    public Recipe(Set<FoodItem> ingredients, Set<FoodItem> addableIngredients, Map<String, Integer> ingredientAmounts, int amountCreated) {
        this.amountCreated = amountCreated;
        this.ingredients = ingredients;
        this.addableIngredients = addableIngredients;
        this.ingredientAmounts = ingredientAmounts;

    }

    /**
     * Gets the ingredient amounts used in the recipe and the ingredient amounts of the addable food items
     * @return the ingredient amounts used in the recipe
     */
    public Map<String, Integer> getIngredientAmounts() { return ingredientAmounts; }

    /**
     * Gets the amount of food items the recipe creates
     * @return type int of amount created
     */
    public int getAmountCreated() {
        return amountCreated;
    }

    /**
     * Gets the set of ingredients in the recipe
     * @return set of food items in the recipe
     */
    public Set<FoodItem> getIngredients(){ return ingredients; }

    /**
     * Gets the set of addable ingredients in the recipe
     * @return set of addable ingredients in the recipe
     */
    public Set<FoodItem> getAddableIngredients(){
        return addableIngredients;
    }

    /**
     * Add an addable food item to the recipe during the order
     * @param ingredient food item from the set of addable ingredients
     */
    public void addIngredient(FoodItem ingredient){
        ingredients.add(ingredient);

    }

    /**
     * Remove an ingredient from a recipe during an order
     * @param code the code of the ingredient
     */
    public void removeIngredient(String code){

    }

    /**
     * Returns if the recipe is vegetarian or not. Returns optionally if the recipe is currently not vegetarian and can be vegetarian
     * @return YES if vegetarian, OPTIONAL if currently not vegetarian and can be made vegetarian, NO otherwise
     */
    public boolean getIsVegetarian() {
        Iterator<FoodItem> itr = ingredients.iterator();
        while (itr.hasNext()) {
            if (itr.next().getIsVegetarian() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if the recipe is vegan or not. Returns optionally if the recipe is currently not vegan and can be vegan
     * @return YES if vegan, OPTIONAL if currently not vegan and can be made vegan, NO otherwise
     */
    public boolean getIsVegan() {
        Iterator<FoodItem> itr = ingredients.iterator();
        while (itr.hasNext()) {
            if (itr.next().getIsVegan() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns if the recipe is glutenFree or not. Returns optionally if the recipe is currently not glutenFree and can be glutenFree
     * @return YES if glutenFree, OPTIONAL if currently not glutenFree and can be made glutenFree, NO otherwise
     */
    public boolean getIsGlutenFree() {
        Iterator<FoodItem> itr = ingredients.iterator();
        while (itr.hasNext()) {
            if (itr.next().getIsGlutenFree() == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns total calories in the recipe
     * @return type double of the number of calories
     */
    public double getCalories() {
        double totalCal = 0;
        Iterator<FoodItem> itr = ingredients.iterator();
        while (itr.hasNext()) {
            totalCal += itr.next().getCaloriesPerUnit();
        }
        return totalCal;
    }





}
