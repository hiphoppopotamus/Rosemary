package seng202.team1.gui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seng202.team1.model.FoodItem;
import seng202.team1.model.Recipe;
import seng202.team1.util.RecipeBuilder;

import java.io.IOException;

public class RecipeView extends VBox {

    @FXML
    private VBox ingredientsVBox;

    @FXML private Button editRecipe, addSelected;

    @FXML private Label addItemErrorMsg;

    private EditDataController parent;
    private RecipeBuilder model;

    public RecipeView() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("recipeDisplay.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initialize() {
        resetView();
        ingredientsVBox.getChildren().add(new Label("no item selected."));
    }

    public void resetView() {
        editRecipe.setText("edit recipe");
        editRecipe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                editRecipe();
            }
        });

        addSelected.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                addSelectedItemInParent();
            }
        });

        resetIngredientList();
    }

    public void setParent(EditDataController parent) {
        this.parent = parent;
    }

    public void updateModel(Recipe modelRecipe) {

        resetView();
        addSelected.setVisible(false);
        model = new RecipeBuilder();
        model.loadExistingRecipeData(modelRecipe);

        if (model == null) {
            ingredientsVBox.getChildren().add(new Label("no recipe for this item."));
        } else {
            refreshIngredientList();
        }
    }

    private void refreshIngredientList() {
        resetIngredientList();
        for (FoodItem ingredient : model.getIngredients()) {
            String name = ingredient.getName();
            int amount = model.getIngredientAmounts().get(ingredient.getCode());
            ingredientsVBox.getChildren().add(new Label(name + " (" + amount + ")"));
        }
    }

    public void editRecipe() {
        addSelected.setVisible(true);
        editRecipe.setText("save recipe");
        editRecipe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                saveChanges();
            }
        });
    }

    private void saveChanges() {
        Recipe newRecipe = model.generateRecipe(1);
        parent.updateSelectedItemRecipe(newRecipe);
        updateModel(newRecipe);
    }

    public void addSelectedItemInParent() {
        FoodItem candidate = parent.getSelectionAsFoodItem();
        if (candidate == null) {
            addItemErrorMsg.setText("no item selected.");
        } else {
            model.addIngredient(candidate, 1);
            refreshIngredientList();
        }
    }

    private void resetIngredientList() {
        ingredientsVBox.getChildren().clear();
    }

}