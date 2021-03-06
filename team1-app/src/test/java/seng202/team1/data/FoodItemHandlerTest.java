package seng202.team1.data;

import org.joda.money.BigMoney;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import seng202.team1.model.FoodItem;
import seng202.team1.util.UnitType;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class FoodItemHandlerTest {

    FoodItemHandler handler;
    String source;
    DocumentBuilder builder;

    @BeforeEach
    void beforeEach() {
        source = "src/test/resources/xml/TESTXML1.xml";
        handler = new FoodItemHandler(source, true);
    }

    @Test
    void testParseTESTXML1() {
        try {
            handler.parseInput();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        FoodItem beefburg = new FoodItem("BEEFBURG", "Hamburger", UnitType.GRAM);
        beefburg.setCost(BigMoney.parse("NZD 21.00"));
        beefburg.setCaloriesPerUnit(295);
        FoodItem cheeseburg = new FoodItem("CHEESEBURG", "Cheeseburger", UnitType.GRAM);
        cheeseburg.setCost(BigMoney.parse("NZD 22.00"));
        cheeseburg.setCaloriesPerUnit(303);
        FoodItem tofuburg = new FoodItem("TOFUBURG", "Tofu Burger", UnitType.GRAM);
        tofuburg.setCost(BigMoney.parse("NZD 50.00"));
        tofuburg.setCaloriesPerUnit(168.4);
        tofuburg.setIsVegetarian(true);
        FoodItem applesoda = new FoodItem("APPLESODA", "Sparkling Apple", UnitType.ML);
        applesoda.setCost(BigMoney.parse("NZD 96.00"));
        applesoda.setCaloriesPerUnit(87);
        applesoda.setIsVegan(true);
        applesoda.setIsGlutenFree(true);

        List<FoodItem> expectedItems = Arrays.asList(
                beefburg, cheeseburg, tofuburg, applesoda
        );
        List<FoodItem> items = new ArrayList<FoodItem>(handler.getFoodItems().values());

        // sorting lists is used for ease of reading if assertEquals(expectedItems, items) fails
        expectedItems.sort((item1, item2) -> item1.getCode().compareTo(item2.getCode()));
        items.sort((item1, item2) -> item1.getCode().compareTo(item2.getCode()));

        assertEquals(expectedItems.size(), items.size());
        assertEquals(expectedItems, items);
    }
}