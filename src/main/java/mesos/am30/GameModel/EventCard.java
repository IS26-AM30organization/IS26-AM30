package mesos.am30.GameModel;

public class EventCard extends Card {
    private final IF_Event event;

    public EventCard(int era, IF_Event event) {
        super(era, 2);
        this.event = event;
    }

    public IF_Event getEvent() {
        return event;
    }
}
