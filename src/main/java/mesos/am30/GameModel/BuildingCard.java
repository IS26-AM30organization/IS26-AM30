package mesos.am30.GameModel;

public class BuildingCard extends Card {
    private final IF_Event event;
    private final EventType eventType;
    private final int foodCost;
    private final int ppGainEnd;


    public BuildingCard(int era, IF_Event event, EventType eventType, int foodCost, int ppGainEnd) {
        super(era);
        this.event = event;
        this.eventType = eventType;
        this.foodCost = foodCost;
        this.ppGainEnd = ppGainEnd;
    }

    public EventType getEventType() {
        return eventType;
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
}
