package seng202.team1.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import seng202.team1.model.FoodItem;

import java.io.IOException;

/**
 * displays a single ingredient of an order item in an OrderItemDisplay.
 */
public class OrderIngredientDisplay extends VBox {

    @FXML
    private Label ingredientName;

    @FXML
    private Button ingredientAction;

    private OrderItemDisplay parent;
    private FoodItem model;

    public OrderIngredientDisplay(OrderItemDisplay parent, FoodItem model) {

        this.parent = parent;
        this.model = model; // keep this above loader.load() or initialize() will throw a NullPointerException

        FXMLLoader loader = new FXMLLoader(getClass().getResource("orderIngredientDisplay.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void initialize() {
        ingredientName.setText(model.getName());

        OrderIngredientDisplay display = this;
        ingredientAction.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                parent.removeIngredient(model, display);
            }
        });
    }



}
