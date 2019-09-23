package seng202.team1.GUI;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import seng202.team1.data.DAOFactory;
import seng202.team1.data.OrderDAO;
import seng202.team1.model.FoodItem;
import seng202.team1.model.Order;
import seng202.team1.util.InvalidOrderStatusException;

import java.io.IOException;

public class CreateOrderDisplay extends VBox {

    @FXML
    private Label orderTotal;

    @FXML
    private Button cancelOrder, submitOrder;

    @FXML
    private Label orderTotalCost, statusText;

    @FXML
    private VBox orderItems;

    private OrderController orderController;
    private Order model;
    private OrderDAO orderStorage;


    public CreateOrderDisplay(OrderController orderController, Order model) {

        this.orderController = orderController;
        this.model = model;
        this.orderStorage = DAOFactory.getOrderDAO();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("createOrderDisplay.fxml"));
        loader.setRoot(this);
        loader.setController(this);

        try {
            loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void initialize() {
        cancelOrder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                closeCreateOrderPanel();
            }
        });
        submitOrder.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                submitOrder();
            }
        });
    }

    /**
     * adds the given FoodItem to the order this component models
     * @param item the FoodItem to add
     */
    public void addItemToOrder(FoodItem item) {
        model.addItem(item);
        orderTotalCost.setText("Order Cost - " + model.getCost().toString());
        orderItems.getChildren().add(new OrderItemDisplay(this, item));
    }

    /**
     * removes the given FoodItem from the order this component models
     * @param item the FoodItem to remove
     */
    public void removeItemFromOrder(FoodItem item, OrderItemDisplay display) {
        model.removeItem(item);
        orderTotalCost.setText("Order Total - " + model.getCost().toString());
        orderItems.getChildren().remove(display);
    }

    /**
     * submits the order and closes the create order display
     */
    public void submitOrder() {
        try {
            model.submitOrder();
            orderStorage.addOrder(model);
        } catch (InvalidOrderStatusException e) {
            statusText.setText("Error submitting order: " + e.getMessage());
            return;
        }
        closeCreateOrderPanel(model);
    }

    /**
     * closes the create order panel
     */
    public void closeCreateOrderPanel() {
        orderController.stopCreatingOrder();
    }

    /**
     * closes the create order panel while also passing an already submitted order to the order controller
     * @param submittedOrder the submitted order to pass to the order controller
     */
    public void closeCreateOrderPanel(Order submittedOrder) {
        orderController.addSubmittedOrderAndClose(submittedOrder);
    }

}
