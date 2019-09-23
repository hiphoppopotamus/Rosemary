package seng202.team1.model;

import org.joda.money.BigMoney;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import seng202.team1.util.InvalidOrderStatusException;
import seng202.team1.util.OrderStatus;
import seng202.team1.model.FoodItem;


import java.math.RoundingMode;
import java.util.*;

import static seng202.team1.util.OrderStatus.*;

/**
 * 
 */
public class Order {

    public static int DEFAULT_ID = -1; // id of orders not yet in storage

    private int ID = DEFAULT_ID;
    private List<FoodItem> foodItems = new ArrayList<FoodItem>();
    private String orderNote;
    private OrderStatus status = OrderStatus.CREATING;

    public int getOrderID() { return ID; }

    public void setId(int id) {
        this.ID = id;
    }

    public String getOrderNote() {
        return orderNote;
    }

    public void setOrderNote(String orderNote) {
        this.orderNote = orderNote;
    }

    /**
     * @param item
     * adds a single instance of the specified item to the foodItems list
     */
    public void addItem(FoodItem item) {

        if (status == CREATING) {
            if (item == null) {
                throw new IllegalArgumentException("A null item cannot be added to an order.");
            } else {
                foodItems.add(item);
            }
        } else {
            throw new InvalidOrderStatusException("Only orders that are still in the creation process can have items added to them");
        }
    }

    /**
     * @param item
     * removes a single instance of the specified item from the foodItems list
     */
    public void removeItem(FoodItem item) {
        if (status == CREATING) {
            if (item == null) {
                throw new IllegalArgumentException("A null item cannot be removed from an order.");
            }
            if (foodItems.size() > 0) {
                foodItems.remove(item);
            } else {
                throw new IllegalArgumentException("Items cannot be removed from an empty order.");
            }
        } else {
            throw new InvalidOrderStatusException("Only orders that are still in the creation process can have items removed from them");
        }
    }

    /**
     * changes the status of the order to cancelled, (registers it in the database?)
     * Only CREATING orders can be cancelled.
     */
    public void submitOrder() {
        if (status == CREATING) {
            if (foodItems.size() > 0) {
                status = SUBMITTED;
            } else {
                throw new InvalidOrderStatusException("cannot submit an empty order");
            }
        }
        else {
            throw new InvalidOrderStatusException("Only orders with the CREATING status can be submitted.");
        }
    }


    /**
     * changes the status of the order to cancelled, (registers it in the database?)
     * Only CREATING orders can be cancelled.
     */
    public void cancelOrder() {
        //if status is still being processed the order can be cancelled
        if (status == SUBMITTED) {
            status = CANCELLED;
        } else {
            throw new InvalidOrderStatusException("Only orders with the SUBMITTED status can be cancelled.");
        }
    }

    /**
     * updates the status of the order to refunded (returns the amount of refunded money calculated by components?)
     * only completed orders can be refunded
     */
    public void refundOrder() {
        if (status == COMPLETED) {
            status = REFUNDED;
        } else {
            throw new InvalidOrderStatusException("Only orders with the COMPLETED status can be refunded.");
        }
    }

    /**
     * changes the status of the order to complete
     * only CREATING orders can be set to complete.
     */
    public void completeOrder() {
        if (status == SUBMITTED) {
            status = COMPLETED;
        } else {
            throw new InvalidOrderStatusException("Only orders with the SUBMITTED status can be completed.");
        }
    }

    /**
     * sets the order status directly. used when loading an existing order from the database.
     * @param status
     */
    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    /**
     * returns the contents of the order in list form
     */
    public List<FoodItem> getOrderContents() {
        return foodItems;
    }

    /**
     * returns the current status of the order
     */
    public OrderStatus getOrderStatus() {
        return status;
    }

    /**
     * returns the cost of the order
     * @return the total cost of the order
     */
    public Money getCost() {
        BigMoney totalCost = BigMoney.parse("NZD 0.00");
        for (FoodItem item : foodItems) {
            totalCost = totalCost.plus(item.getCost());
        }
        return totalCost.toMoney(RoundingMode.HALF_UP);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return ID == order.ID &&
                foodItems.equals(order.foodItems) &&
                Objects.equals(orderNote, order.orderNote) &&
                status == order.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ID, foodItems, orderNote, status);
    }

    @Override
    public String toString() {
        return "Order{" +
                "ID=" + ID +
                ", foodItems=" + foodItems +
                ", orderNote='" + orderNote + '\'' +
                ", status=" + status +
                '}';
    }
}

