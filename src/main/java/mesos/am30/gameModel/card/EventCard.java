package mesos.am30.gameModel.card;

import mesos.am30.common.TColors;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;

/**
 * Representation of an Event Card.
 * <br/>This Class works as the representation for an Event Card, independently of its Event.
 */
public class EventCard extends Card {
    private final IF_Event event;

    /**
     * Constructor of an Event Card.
     * <br/><strong>Pre:</strong> 1 &lt;= era &lt;= 4 &amp;&amp; id > 1 &amp;&amp; event != null
     * <br/><strong>Post:</strong> this.era = era &amp;&amp; this.id = id &amp;&amp; this.event = event
     *
     * @param era   Era when the Card is draw.
     * @param id    Unique ID of the Card.
     * @param event Event assigned to the Card.
     */
    public EventCard(int era, int id, IF_Event event) {
        super(era, id);
        this.event = event;
    }

    /**
     * Getter for the attribute "event".
     *
     * @return Event assigned to the Card.
     */
    public IF_Event getEvent() {
        return event;
    }

    /**
     * @see Card Event Card implementation of the drawUp method.
     */
    @Override
    public void drawUp(Board board){
        board.drawUp(this);
    }

    /**
     * @see Card Event Card implementation of the drawDown method.
     * <br/>This implementation calls the DrawUp method, since Event Cards will always be added to the upper row.
     */
    @Override
    public void drawDown(Board board){
        drawUp(board);
    }

    /**
     * @see Card Event Card implementation of the discard method.
     */
    @Override
    public void discard(Board board){
        for (Player player : board.getPlayersOrder()) {
            event.handleEvent(player);
        }
        board.discard(this);
    }

    /**
     * @see Card Event Card implementation of the createRow method.
     */
    @Override
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();

        event.getAttributes(str1, str2, str3);
        str1.append(" ").append("⚠");

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
        eventRole.repeat(" ", Math.max(0, maxWidth - str1.length()));

        ln2.append(str2);
        ln2.repeat(" ", Math.max(0, maxWidth - str2.length()));

        ln3.append(str3);
        ln3.repeat(" ", Math.max(0, maxWidth - str3.length()));
    }

    /**
     * @see Card Event Card implementation of the getCardInfo method.
     */
    @Override
    public String getCardInfo(StringBuilder info) {
        return event.getInfo(info);
    }

    /**
     * @see Card Event Card implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return event.getArt();
    }
}
