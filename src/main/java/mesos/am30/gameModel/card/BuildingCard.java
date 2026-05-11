package mesos.am30.gameModel.card;

import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;

import java.util.Objects;

public class BuildingCard extends Card {
    private final IF_Event event;
    private final EventType eventType;
    private final int foodCost;
    private final int ppGainEnd;


    public BuildingCard(int era, IF_Event event, EventType eventType, int foodCost, int ppGainEnd, int id) {
        super(era, id);
        this.event = event;
        this.eventType = eventType;
        this.foodCost = foodCost;
        this.ppGainEnd = ppGainEnd;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getPpGainEnd() {
        return ppGainEnd;
    }

    public IF_Event getEvent() {
        return event;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean isPickable() {
        return true;
    }

    /**
     * Checks whether a player has enough food to buy the building card
     * @param player
     * @return T if he can, F if he cannot
     */
    public boolean canBeBought(Player player) {
        return player.getParameters().get(Parameter.FOOD)
                + player.getParameters().get(Parameter.BUILDER) >= foodCost;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        BuildingCard that = (BuildingCard) o;
        return foodCost == that.foodCost && ppGainEnd == that.ppGainEnd && Objects.equals(event, that.event) && eventType == that.eventType;
    }

    @Override
    public void discard(Board board) {
        if(board.getLowerBuildings().contains(this))
            board.getLowerBuildings().remove(this);
        else if (board.getUpperBuildings().contains(this))
            board.getUpperBuildings().remove(this);
    }

    @Override
    public int hashCode() {
        return Objects.hash(event, eventType, foodCost, ppGainEnd);
    }

    @Override
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();

        if (foodCost != 0) str2.append("fCost: " + foodCost);
        if (ppGainEnd != 0) str2.append("ppGainEnd: " + ppGainEnd);

        event.getAttributes(str1, str2, str3);

        //need to take longest word
        int maxWidth = str1.length();
        if (str2.length() > maxWidth) {
            maxWidth = str2.length();
        }
        if (str3.length() > maxWidth) {
            maxWidth = str3.length();
        }
        maxWidth += 3;

        eventRole.append(str1).append("\uD83C\uDFE0");;
        for (int x = str1.length(); x < maxWidth-2; x++) eventRole.append(" ");

        ln2.append(str2);
        for (int x = str2.length(); x < maxWidth; x++) ln2.append(" ");

        ln3.append(str3);
        for (int x = str3.length(); x < maxWidth; x++) ln3.append(" ");
    }

}
