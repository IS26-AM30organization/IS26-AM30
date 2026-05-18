package mesos.am30.gameModel.board;

import com.google.gson.reflect.TypeToken;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.card.BuildingCard;
import mesos.am30.gameModel.card.CharacterCard;
import mesos.am30.gameModel.card.EventCard;
import mesos.am30.gameModel.card.Tile;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UtilityTest {

     @Test
     void wrongFile() {
         int playerNum = 5;
         Type type = new TypeToken<CharacterCard>(){}.getType();
         assertThrows(IllegalArgumentException.class, () -> Utility.cardLoader("wrong", playerNum, type));
     }

     @Test
     void wrongPlayersNumber() throws IOException {
         int playerNum = 1;
         Type type = new TypeToken<CharacterCard>(){}.getType();
         List<CharacterCard> cards = Utility.cardLoader("characters.json", playerNum, type);
         assertTrue(cards.isEmpty());
     }

    @Test
    void testCharacterLoader() throws Exception {
        int playerNum = 5;
        Type type = new TypeToken<CharacterCard>(){}.getType();

        List<CharacterCard> cards = Utility.cardLoader("characters.json", playerNum, type);

        assertNotNull(cards);

        int i = 0;
        for (CharacterCard card : cards) {
            i++;
            System.out.println("Era: " + card.getEra() + " PP: " + card.getRole());
        }
        System.out.println("#Cards: " + i);
    }

    @Test
    void testBuildingsDeserialization() throws Exception {
        int playerNum = 5;
        Type type = new TypeToken<BuildingCard>(){}.getType();

        List<BuildingCard> buildings = Utility.cardLoader("buildings.json", playerNum, type);

        assertFalse(buildings.isEmpty(), "At least 1 building");

        int i = 0;
        for (BuildingCard b : buildings) {
            IF_Event build = b.getEvent();
            assertNotNull(build, "Event should not be null");
            i++;
            System.out.println("Building's Era " + b.getEra() + " has Event: " + build.getClass().getSimpleName());
        }
        System.out.println("#Cards: " + i);
    }

    @Test
    void testEventDeserialization() throws Exception {
        int playerNum = 5;
        Type type = new TypeToken<EventCard>(){}.getType();

        List<EventCard> eventC = Utility.cardLoader("events.json", playerNum, type);

        assertFalse(eventC.isEmpty(), "At least 1 event");

        int i = 0;
        for (EventCard e : eventC) {
            IF_Event event = e.getEvent();
            assertNotNull(event, "Event should not be null");
            i++;
            System.out.println("Event's Era " + e.getEra() + " is Event: " + event.getClass().getSimpleName());
        }
        System.out.println("#Cards: " + i);
    }

    @Test
    void testTilesDeserialization() throws Exception {
        int playerNum = 5;
        Type type = new TypeToken<Tile>(){}.getType();

        List<Tile> tile = Utility.cardLoader("tiles.json", playerNum, type);

        assertFalse(tile.isEmpty(), "At least 1 tile");

        int i = 0;
        for (Tile t : tile) {
            i++;
            System.out.println("Tile's Up: " + t.getUpArrows() + " Down: " + t.getDownArrows() + " Food: " + t.getFood());
        }
        System.out.println("#Cards: " + i);
    }

}