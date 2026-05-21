package mesos.am30.gameModel.card;

import mesos.am30.common.TColors;
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

    /**
     * Removes the event card from the row once it's event has been handled
     */
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
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();

        event.getAttributes(str1, str2, str3);
        str1.append(" ").append("\u26A0");

        //need to take longest word
        int maxWidth = str1.length();
        if (str2.length() > maxWidth) {
            maxWidth = str2.length();
        }
        if (str3.length() > maxWidth) {
            maxWidth = str3.length();
        }
        maxWidth += 5;

        eventRole.append(TColors.RED).append(str1).append(TColors.RESET);
        for (int x = str1.length(); x < maxWidth; x++) eventRole.append(" ");

        ln2.append(str2);
        for (int x = str2.length(); x < maxWidth; x++) ln2.append(" ");

        ln3.append(str3);
        for (int x = str3.length(); x < maxWidth; x++) ln3.append(" ");
    }

    @Override
    public String getArt(){
        return event.getArt();
    }
}
