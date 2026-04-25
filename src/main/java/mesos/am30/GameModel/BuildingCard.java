package mesos.am30.GameModel;

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
}
