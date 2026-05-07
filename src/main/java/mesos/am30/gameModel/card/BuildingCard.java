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
    public void displayCard() {
        System.out.println(String.format("%-10s | %-5d | %-5d",
                eventType, foodCost, ppGainEnd));
    }

    @Override
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        String r = "[ build ]";
        String i = "";
        if (foodCost != 0) i = "fCost: " + foodCost;
        String pp = "";
        if (ppGainEnd != 0) pp = "ppGainEnd: " + ppGainEnd;

        //need to take longest word
        int maxWidth = r.length();
        if (i.length() > maxWidth) {
            maxWidth = i.length();
        }
        if (pp.length() > maxWidth) {
            maxWidth = pp.length();
        }
        maxWidth += 3;

        eventRole.append(r);
        for (int x = r.length(); x < maxWidth; x++) eventRole.append(" ");

        ln2.append(i);
        for (int x = i.length(); x < maxWidth; x++) ln2.append(" ");

        ln3.append(pp);
        for (int x = pp.length(); x < maxWidth; x++) ln3.append(" ");
    }
}
