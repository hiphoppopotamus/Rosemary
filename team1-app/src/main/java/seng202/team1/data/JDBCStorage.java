package seng202.team1.data;

import org.apache.ibatis.io.Resources;
import org.joda.money.BigMoney;
import seng202.team1.model.FoodItem;
import seng202.team1.model.Order;
import seng202.team1.util.InvalidDataCodeException;
import seng202.team1.util.OrderStatus;
import seng202.team1.util.UnitType;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.sql.*;
import java.util.*;

public class JDBCStorage implements FoodItemDAO, OrderDAO {

    private static String url = "jdbc:sqlite:rosemary.db";
    private static JDBCStorage instance;

    private JDBCStorage() {
        createAllTables();
    }

    /**
     * function used to get an instance of a InMemoryDAO object.
     *Implements the singleton pattern
     * @return an instance of InMemoryDAO
     */
    public static JDBCStorage getInstance() {
        if (instance == null) {
            instance = new JDBCStorage();
        }
        return instance;
    }

    /**
     * deletes all items from all tables in the database instance. used for testing purposes.
     */
    public void resetInstance() {
        String sql = "DELETE FROM FoodItem; DELETE FROM Recipe; DELETE FROM RecipeContains";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create the table used to store FoodItems in the database if it does not already exist.
     * will create the database if an existing database is not found.
     */
    private static void createFoodItemTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS FoodItem\n" +
                "(Id /* primary key ID. added because varchar prim key isn't good? */ INTEGER PRIMARY KEY,\n" +
                " Code /* alphanumberic code of the FoodItem */ VARCHAR(10) UNIQUE NOT NULL\n" +
                "    CONSTRAINT code_min_size CHECK (LENGTH(Code) >= 3),\n" +
                " Name /* the FoodItem's name */ VARCHAR(20) NOT NULL\n" +
                "    CONSTRAINT name_min_size CHECK (LENGTH(Name) >= 3),\n" +
                " UnitType /* unit type (count, ml or gram) */ CHAR(1) NOT NULL\n" +
                "    CONSTRAINT check_unit CHECK (UnitType in ('c', 'm', 'g')),\n" +
                "Cost /* cost of the FoodItem for customers */ VARCHAR(8000) NOT NULL DEFAULT '0',\n" +
                " StockLevel /* the current amount of this item stored */ INTEGER NOT NULL DEFAULT 0,\n" +
                " IsVegetarian /* whether the item is vegetarian */ BIT NOT NULL DEFAULT 0,\n" +
                " IsVegan /* whether the item is vegan */ BIT NOT NULL DEFAULT 0,\n" +
                " IsGlutenFree /* whether the item is gluten free */ BIT NOT NULL DEFAULT 0,\n" +
                " CalPerUnit /* number of calories per single unit */ VARCHAR(8000));";

        try (Connection conn = DriverManager.getConnection(url); // will create DB if doesn't exist
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create the table used to store Recipes in the database if it does not already exist.
     * will create the database if an existing database is not found.
     */
    private static void createRecipeTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Recipe\n" +
                "(Id /* unique identifier for a Recipe */ INTEGER,\n" +
                " Product /* the Id of the FoodItem created by the Recipe */ INT NOT NULL REFERENCES FoodItem,\n" +
                " AmountCreated /* the amount of the Product created by the Recipe */ INT NOT NULL,\n" +
                " PRIMARY KEY (Id, Product));";

        try (Connection conn = DriverManager.getConnection(url); // will create DB if doesn't exist
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create the table used to store the relationship between Recipes and FoodItems contained in it in the database if it does not already exist.
     * will create the database if an existing database is not found.
     */
    private static void createRecipeContainsTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS RecipeContains\n" +
                "(Recipe /* Id of the Recipe */ INT NOT NULL REFERENCES Recipe,\n" +
                " FoodItem /* FoodItem contained in the recipe */ INT NOT NULL REFERENCES FoodItem,\n" +
                " Amount /* the amount of the FoodItem in the Recipe */ INT NOT NULL,\n" +
                " PRIMARY KEY (Recipe, FoodItem));";

        try (Connection conn = DriverManager.getConnection(url); // will create DB if doesn't exist
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create the table used to store Orders in the database if it does not already exist.
     * will create the database if an existing database is not found.
     */
    private static void createOrderTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS CustomerOrder\n" +
                "(Id /* unique identifier for an Order */ INTEGER PRIMARY KEY,\n" +
                " Status /* the status of the order (Creating='c', Submitted='s', Completed='d', Cancelled='x', Refunded='r') */ CHAR(1) NOT NULL\n" +
                "    CONSTRAINT check_status CHECK (Status in ('c', 's', 'd', 'x', 'r')),\n" +
                " Note /* any notes added to the order */ VARCHAR(8000),\n" +
                " LastUpdated /* the time the order's status was last updated in unix time */ DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,\n" +
                " Location /* the location the order was processed if known */ VARCHAR(8000),\n" +
                " Weather /* the location the order was processed if known */ VARCHAR(8000));";

        try (Connection conn = DriverManager.getConnection(url); // will create DB if doesn't exist
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create the table used to store the relationship between Orders and FoodItems contained in it in the database if it does not already exist.
     * will create the database if an existing database is not found.
     */
    private static void createOrderContainsTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS OrderContains\n" +
                "(CustomerOrder /* Id of the Order*/ INTEGER NOT NULL REFERENCES CustomerOrder,\n" +
                " FoodItem /* Id of the FoodItem contained in the Order */ INTEGER NOT NULL REFERENCES FoodItem,\n" +
                " PRIMARY KEY (CustomerOrder, FoodItem));";

        try (Connection conn = DriverManager.getConnection(url); // will create DB if doesn't exist
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * create the table used to store instances of FoodItems ordered by customers in the database if it does not already exist.
     * will create the database if an existing database is not found.
     */
    private static void createOrderedFoodItemTable() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS OrderedFoodItem\n" +
                "(Id /* primary key ID */ INTEGER PRIMARY KEY,\n" +
                " Code /* alphanumberic code of the FoodItem */ VARCHAR(10) NOT NULL\n" +
                "    CONSTRAINT code_min_size CHECK (LENGTH(Code) >= 3),\n" +
                " Name /* the FoodItem's name */ VARCHAR(20) NOT NULL\n" +
                "    CONSTRAINT name_min_size CHECK (LENGTH(Name) >= 3),\n" +
                " UnitType /* unit type (count, ml or gram) */ CHAR(1) NOT NULL\n" +
                "    CONSTRAINT check_unit CHECK (UnitType in ('c', 'm', 'g')),\n" +
                " Cost /* cost of the FoodItem for customers */ VARCHAR(8000) NOT NULL DEFAULT '0',\n" +
                " IsVegetarian /* whether the item is vegetarian */ BIT NOT NULL DEFAULT 0,\n" +
                " IsVegan /* whether the item is vegan */ BIT NOT NULL DEFAULT 0,\n" +
                " IsGlutenFree /* whether the item is gluten free */ BIT NOT NULL DEFAULT 0,\n" +
                " CalPerUnit /* number of calories per single unit */ VARCHAR(8000));";

        try (Connection conn = DriverManager.getConnection(url); // will create DB if doesn't exist
             Statement stmt = conn.createStatement()) {
            // create a new table
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * creates all tables used in the database
     */
    private void createAllTables() {
        createFoodItemTable();
        createRecipeTable();
        createRecipeContainsTable();
        createOrderTable();
        createOrderContainsTable();
        createOrderedFoodItemTable();
    }

    /**
     * reads a single FoodItem from a ResultSet from the FoodItem table
     * @param rs the ResultSet from FoodItem
     * @return a single FoodItem with the properties stored in the database
     */
    private FoodItem readFoodItem(ResultSet rs) {
        String code, name, cost, unitType, calPerUnit;
        boolean isVegetarian, isVegan, isGlutenFree;

        try {
            code = rs.getString("Code");
            name = rs.getString("Name");
            unitType = rs.getString("UnitType");
            cost = rs.getString("Cost");
            isVegetarian = rs.getBoolean("IsVegetarian");
            isVegan = rs.getBoolean("IsVegan");
            isGlutenFree = rs.getBoolean("IsGlutenFree");
            calPerUnit = rs.getString("CalPerUnit");

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        UnitType unit;
        switch (unitType) {
            case "g":
                unit = UnitType.GRAM;
                break;
            case "m":
                unit = UnitType.ML;
                break;
            default:
                unit = UnitType.COUNT;
                break;
        }
        FoodItem result = new FoodItem(code, name, unit);
        result.setCost(BigMoney.parse(cost));
        result.setIsVegetarian(isVegetarian);
        result.setIsVegan(isVegan);
        result.setIsGlutenFree(isGlutenFree);
        result.setCaloriesPerUnit(Double.parseDouble(calPerUnit));
        return result;
    }

    /**
     * reads a single Order from a ResultSet from the CustomerOrder table
     * @param rs the ResultSet from CustomerOrder
     * @return a single Order with the properties stored in the database
     */
    private Order readOrder(ResultSet rs) {
        int id;
        List<FoodItem> foodItems = new ArrayList<FoodItem>();
        String orderNote, statusString;

        try {
            id = rs.getInt("Id");
            orderNote = rs.getString("Note");
            statusString = rs.getString("Status");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }

        OrderStatus status;
        switch (statusString) {
            case "c":
                status = OrderStatus.CREATING;
                break;
            case "s":
                status = OrderStatus.SUBMITTED;
                break;
            case "x":
                status = OrderStatus.CANCELLED;
                break;
            case "r":
                status = OrderStatus.REFUNDED;
                break;
            default:
                status = OrderStatus.COMPLETED;
                break;
        }
        Order result = new Order();
        result.setId(id);
        result.setOrderNote(orderNote);

        String sql = "SELECT *\n" +
                "FROM OrderContains JOIN OrderedFoodItem ON FoodItem = Id\n" +
                "WHERE CustomerOrder = ?";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, result.getOrderID());
                ResultSet rs2 = pstmt.executeQuery();

                while (rs2.next()) {
                    result.addItem(readFoodItem(rs2));
                }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        result.setStatus(status);

        return result;
    }

    /**
     * read the stock level of a FoodItem from the database given a ResultSet from the FoodItem table
     * @param rs the ResultSet from FoodItem
     * @return an integer stock count
     */
    private int readFoodItemStock(ResultSet rs) {
        try {
            return rs.getInt("StockLevel");
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public Set<FoodItem> getAllFoodItems() {
        String sql = "SELECT * FROM FoodItem";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            Set<FoodItem> items = new HashSet<FoodItem>();
            while (rs.next()) {
                items.add(readFoodItem(rs));
            }
            return items;

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public FoodItem getFoodItemByCode(String code) {
        String sql = "SELECT * FROM FoodItem WHERE Code = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() == false) {
                return null;
            } else {
                return readFoodItem(rs);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    @Override
    public void addFoodItem(FoodItem item, int count) {
        if (getFoodItemByCode(item.getCode()) != null) {
            throw new InvalidDataCodeException("FoodItem with code " + item.getCode() + " already exists in the database.");
        }

        String sql = "INSERT INTO FoodItem(Code, Name, Cost, UnitType, StockLevel, IsVegetarian, IsVegan, IsGlutenFree, CalPerUnit) VALUES(?,?,?,?,?,?,?,?,?)";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, item.getCode());
            pstmt.setString(2, item.getName());
            pstmt.setString(3, item.getCost().toString());
            pstmt.setString(4, item.getUnit().toString());
            pstmt.setInt(5, count);
            pstmt.setBoolean(6, item.getIsVegetarian());
            pstmt.setBoolean(7, item.getIsVegan());
            pstmt.setBoolean(8, item.getIsGlutenFree());
            pstmt.setString(9, Double.toString(item.getCaloriesPerUnit()));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void updateFoodItem(FoodItem alteredItem) {
        if (getFoodItemByCode(alteredItem.getCode()) == null) {
            throw new InvalidDataCodeException("item with given item's code does not exist in storage.");
        }

        String sql = "UPDATE FoodItem\n" +
                "SET Name=?, Cost=?, UnitType=?, IsVegetarian=?, IsVegan=?, IsGlutenFree=?, CalPerUnit=?\n" +
                "WHERE Code=?";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, alteredItem.getName());
            pstmt.setString(2, alteredItem.getCost().toString());
            pstmt.setString(3, alteredItem.getUnit().toString());
            pstmt.setBoolean(4, alteredItem.getIsVegetarian());
            pstmt.setBoolean(5, alteredItem.getIsVegan());
            pstmt.setBoolean(6, alteredItem.getIsGlutenFree());
            pstmt.setString(7, Double.toString(alteredItem.getCaloriesPerUnit()));
            pstmt.setString(8, alteredItem.getCode());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void removeFoodItem(String code) {
        if (getFoodItemByCode(code) == null) {
            throw new InvalidDataCodeException("no FoodItem with the given code exists in storage.");
        }

        String sql = "DELETE FROM FoodItem\n" +
                "WHERE Code=?";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void setFoodItemStock(String code, int count) {
        if (count < 0) {
            throw new IllegalArgumentException("count must be non-negative.");
        }
        if (getFoodItemByCode(code) == null) {
            throw new InvalidDataCodeException("no FoodItem with given code exists in the database.");
        }

        String sql = "UPDATE FoodItem\n" +
                "SET StockLevel=?\n" +
                "WHERE Code=?";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, count);
            pstmt.setString(2, code);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public int getFoodItemStock(String code) {
        if (getFoodItemByCode(code) == null) {
            throw new InvalidDataCodeException("no FoodItem with given code exists in the database.");
        }

        String sql = "SELECT StockLevel FROM FoodItem WHERE Code = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, code);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() == false) {
                return 0;
            } else {
                return readFoodItemStock(rs);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return 0;
        }
    }

    @Override
    public Set<Order> getAllOrders() {
        return null;
    }

    @Override
    public Order getOrderByID(int id) {
        if (id == Order.DEFAULT_ID) {
            throw new InvalidDataCodeException("given order has the default id " + Order.DEFAULT_ID + ". set a valid ID");
        }

        String sql = "SELECT * FROM CustomerOrder WHERE Id = ? LIMIT 1";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next() == false) {
                return null;
            } else {
                return readOrder(rs);
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private void addOrderedFoodItem(Order order, FoodItem item) {
        String sql = "INSERT INTO OrderedFoodItem(Code, Name, Cost, UnitType, IsVegetarian, IsVegan, IsGlutenFree, CalPerUnit) VALUES(?,?,?,?,?,?,?,?)";

        int insertedId;

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, item.getCode());
            pstmt.setString(2, item.getName());
            pstmt.setString(3, item.getCost().toString());
            pstmt.setString(4, item.getUnit().toString());
            pstmt.setBoolean(5, item.getIsVegetarian());
            pstmt.setBoolean(6, item.getIsVegan());
            pstmt.setBoolean(7, item.getIsGlutenFree());
            pstmt.setString(8, Double.toString(item.getCaloriesPerUnit()));
            pstmt.executeUpdate();
            ResultSet rs = pstmt.getGeneratedKeys();
            insertedId = rs.getInt(1);

            String sql2 = "INSERT INTO OrderContains(CustomerOrder, FoodItem) VALUES (?,?)";

            try (PreparedStatement pstmt2 = conn.prepareStatement(sql2)) {
                pstmt2.setInt(1, order.getOrderID());
                pstmt2.setInt(2, insertedId);
                pstmt2.executeUpdate();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void addOrder(Order order) {
        if (order == null) {
            throw new NullPointerException();
        }

        if (order.getOrderContents().size() == 0) {
            throw new IllegalArgumentException("cannot add an empty order");
        }

        if (order.getOrderID() != Order.DEFAULT_ID) {
            throw new InvalidDataCodeException("cannot add order with a non-default id. please use update");
        }

        String insertOrder = "INSERT INTO CustomerOrder(Status, Note) VALUES (?,?)";
        //String insertOrderItem = "INSERT INTO OrderContains(CustomerOrder, FoodItem)"

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(insertOrder, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, order.getOrderStatus().toString());
            pstmt.setString(2, order.getOrderNote());

            pstmt.executeUpdate();
            order.setId(pstmt.getGeneratedKeys().getInt(1));

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }

        for (FoodItem item : order.getOrderContents()) {
            addOrderedFoodItem(order, item);
        }
    }

    @Override
    public void updateOrder(Order alteredOrder) {
        if (getOrderByID(alteredOrder.getOrderID()) == null) {
            throw new InvalidDataCodeException("order with given order's code does not exist in the database.");
        }

        String sql = "UPDATE CustomerOrder\n" +
                "SET Status=?\n" +
                "WHERE Id=?";

        try (Connection conn = DriverManager.getConnection(JDBCStorage.url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, alteredOrder.getOrderStatus().toString());
            pstmt.setInt(2, alteredOrder.getOrderID());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    @Override
    public void removeOrder(int ID) {

    }


}
