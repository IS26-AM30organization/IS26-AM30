package mesos.am30.gameModel.card;

import mesos.am30.common.TColors;
import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;

/**
 * Representation of a Building Card.
 * <br/>This Class works as the representation for a Building Card, independently of its type of Event.
 */
public class BuildingCard extends Card {
    private final IF_Event event;
    private final EventType eventType;
    private final int foodCost;
    private final int ppGain;

    /**
     * Constructor of a Building Card.
     * <br/><strong>Pre:</strong> 1 <= era <= 4 && id > 1 && event != null && eventType != null && foodCost > 0 && ppGainEnd >= 0
     * <br/><strong>Post:</strong> this.era = era && this.id = id && this.event = event && this.eventType = eventType
     *                              && this.foodCost = foodCost && this.ppGainEnd = ppGainEnd
     *
     * @param era       Era when the Card is draw.
     * @param id        Unique ID of the Card.
     * @param event     Event assigned to the Card.
     * @param eventType Specific type of the Event.
     * @param foodCost  Cost in food of the Card.
     * @param ppGain    Number of PP (Prestige Points) gained at the end of the Game by having this Card.
     */
    public BuildingCard(int era, int id, IF_Event event, EventType eventType, int foodCost, int ppGain) {
        super(era, id);
        this.event = event;
        this.eventType = eventType;
        this.foodCost = foodCost;
        this.ppGain = ppGain;
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
     * Getter for the attribute "eventType".
     *
     * @return Specific type of the Event.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Getter for the attribute "foodCost".
     *
     * @return Cost in food of the Card.
     */
    public int getFoodCost() {
        return foodCost;
    }

    /**
     * Getter for the attribute "ppGain".
     *
     * @return Number of PP (Prestige Points) gained at the end of the Game by having this Card.
     */
    public int getPpGain() {
        return ppGain;
    }

    /**
     * @see Card Building Card implementation of the isPickable method.
     */
    public boolean isPickable() {
        return true;
    }

    /**
     * @see Card Building Card implementation of the drawUp method.
     * <br/>This method does nothing on a Building Card.
     */
    @Override
    public void drawUp(Board board) { /* does nothing for Building Card */ }

    /**
     * @see Card Building Card implementation of the drawDown method.
     * <br/>This method does nothing on a Building Card.
     */
    @Override
    public void drawDown(Board board) { /* does nothing for Building Card */ }

    /**
     * @see Card Building Card implementation of the discard method.
     */
    @Override
    public void discard(Board board) {
        board.discard(this);
    }

    /**
     * @see Card Building Card implementation of the createRow method.
     */
    @Override
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();

        if (foodCost != 0) str2.append("fCost: ").append(foodCost);
        if (ppGain != 0) str2.append("ppGainEnd: ").append(ppGain);

        event.getAttributes(str1, str2, str3);
        str1.append(" ").append("⛫");

        //need to take longest word
        int maxWidth = str1.length();
        if (str2.length() > maxWidth) {
            maxWidth = str2.length();
        }
        if (str3.length() > maxWidth) {
            maxWidth = str3.length();
        }
        maxWidth += 3;

        eventRole.append(TColors.BROWN).append(str1).append(TColors.RESET);
        eventRole.repeat(" ", Math.max(0, maxWidth - (str1.length() + 1)));

        ln2.append(str2);
        ln2.repeat(" ", Math.max(0, maxWidth - str2.length()));

        ln3.append(str3);
        ln3.repeat(" ", Math.max(0, maxWidth - str3.length()));
    }

    /**
     * @see Card Building Card implementation of the getCardInfo method.
     */
    @Override
    public String getCardInfo(StringBuilder info) {
        return event.getInfo(info);
    }


    /**
     * @see Card Building Card implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return ppGain + event.getArt() + foodCost;
    }

    /**
     * Check if a Player has enough food to buy the Building Card.
     * <br/><strong>Pre:</strong> player != null
     *
     * @param player Player who want to buy the building Card.
     *
     * @return True if the Player can buy the Building Card, false otherwise.
     */
    public boolean canBeBought(Player player) {
        return player.getParameters().get(Parameter.FOOD)
                + player.getParameters().get(Parameter.BUILDER) >= foodCost;
    }
}
