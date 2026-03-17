package mesos.am30.GameModel;

public class BuildingCard extends Card {
    private final IF_Event event;
    private final EventType eventType;

    public BuildingCard(int era, IF_Event event, EventType eventType) {
        super(era, 2);
        this.event = event;
        this.eventType = eventType;
    }

    public IF_Event getEvent() {
        return event;
    }

    public EventType getEventType() {
        return eventType;
    }
}
