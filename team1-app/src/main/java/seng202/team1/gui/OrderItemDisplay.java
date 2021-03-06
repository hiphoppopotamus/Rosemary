package seng202.team1.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seng202.team1.model.FoodItem;

import java.io.IOException;

/**
 * displays a single order item in a CreateOrderDisplay.
 */
public class OrderItemDisplay extends VBox {

    @FXML
    private Label itemName;

    @FXML
    private Button removeItem;

    @FXML
    private VBox ingredients;

    private CreateOrderDisplay createOrderDisplay;
    private FoodItem model;

    public OrderItemDisplay(CreateOrderDisplay createOrderDisplay, FoodItem model) {

        this.createOrderDisplay = createOrderDisplay;
        this.model = model; // keep this above loader.load() or initialize() will throw a NullPointerException

        FXMLLoader loader = new FXMLLoader(getClass().getResource("orderItemDisplay.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void initialize() {
        itemName.setText(model.getName());

        if (model.getRecipe() != null) {
            for (FoodItem ingredient : model.getRecipe().getIngredients()) {
                ingredients.getChildren().add(new OrderIngredientDisplay(this, ingredient));
            }
        }

        OrderItemDisplay display = this;
        removeItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                createOrderDisplay.removeItemFromOrder(model, display);
            }
        });

    }

    protected void removeIngredient(FoodItem ingredient, OrderIngredientDisplay display) {
        model.getRecipe().removeIngredient(ingredient.getCode());
        ingredients.getChildren().remove(display);
    }

}
