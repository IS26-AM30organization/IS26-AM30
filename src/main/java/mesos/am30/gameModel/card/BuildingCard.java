package mesos.am30.gameModel.card;

import mesos.am30.common.TColors;
import mesos.am30.gameModel.EventType;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.board.Board;

public class BuildingCard extends Card {
    private final IF_Event event;
    private final EventType eventType;
    private final int foodCost;
    private final int ppGain;


    public BuildingCard(int era, IF_Event event, EventType eventType, int foodCost, int ppGain, int id) {
        super(era, id);
        this.event = event;
        this.eventType = eventType;
        this.foodCost = foodCost;
        this.ppGain = ppGain;
    }

    public int getFoodCost() {
        return foodCost;
    }

    public int getPpGainEnd() {
        return ppGain;
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

    /**
     * Checks whether a player has enough food to buy the building card
     * @param player
     * @return T if he can, F if he cannot
     */
    public boolean canBeBought(Player player) {
        return player.getParameters().get(Parameter.FOOD)
                + player.getParameters().get(Parameter.BUILDER) >= foodCost;
    }

    @Override
    public void discard(Board board) {
        if(board.getLowerBuildings().contains(this))
            board.getLowerBuildings().remove(this);
        else if (board.getUpperBuildings().contains(this))
            board.getUpperBuildings().remove(this);
    }

    @Override
    public void createRow(StringBuilder eventRole, StringBuilder ln2, StringBuilder ln3) {
        StringBuilder str1 = new StringBuilder();
        StringBuilder str2 = new StringBuilder();
        StringBuilder str3 = new StringBuilder();

        if (foodCost != 0) str2.append("fCost: " + foodCost);
        if (ppGain != 0) str2.append("ppGainEnd: " + ppGain);

        event.getAttributes(str1, str2, str3);
        str1.append(" ").append("\u26EB");

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
        for (int x = str1.length()+1; x < maxWidth; x++) eventRole.append(" ");

        ln2.append(str2);
        for (int x = str2.length(); x < maxWidth; x++) ln2.append(" ");

        ln3.append(str3);
        for (int x = str3.length(); x < maxWidth; x++) ln3.append(" ");
    }

    @Override
    public String getArt(){
        return new String(ppGain+""+event.getArt()+""+foodCost);
    }
    @Override
    public String getCardInfo(StringBuilder info) {
        return event.getCardInfo(info);
    }
}
