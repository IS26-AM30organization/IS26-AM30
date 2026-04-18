package mesos.am30.GameModel;

public class EventCard extends Card {
    private final IF_Event event;

    public EventCard(int era, IF_Event event) {
        super(era);
        this.event = event;
    }

    public IF_Event getEvent() {
        return event;
    }

    protected void drawUp(Board board){
        board.drawUp(this);
    }

    protected void drawDown(Board board){
        drawUp(board);
    }

    protected void discard(Board board){
        for (Player player : board.getPlayersOrder()) {
            event.handleEvent(player);
        }
        board.discard(this);
    }

    @Override
    protected void reorder(Board board) {
        super.reorder(board);
    }
}
