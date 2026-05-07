package mesos.am30.gameModel.card;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;

public class EventCard extends Card {
    private final IF_Event event;

    public EventCard(int era, IF_Event event, int id) {
        super(era, id);
        this.event = event;
    }

    public IF_Event getEvent() {
        return event;
    }

    public void drawUp(Board board){
        board.drawUp(this);
    }

    public void drawDown(Board board){
        drawUp(board);
    }

    public void discard(Board board){
        for (Player player : board.getPlayersOrder()) {
            event.handleEvent(player);
        }
        board.discard(this);
    }

    @Override
    public void reorder(Board board) {
        super.reorder(board);
    }

    @Override
    public void displayCard() {
        System.out.println(String.format("event"));
    }

    @Override
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        String r = "[ eventCard ]";
        String i = "";
        String pp = "";

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
