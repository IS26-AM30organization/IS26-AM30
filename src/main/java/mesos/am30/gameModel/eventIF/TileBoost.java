package mesos.am30.gameModel.eventIF;

import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.SpecialBuff;

/**
 * Event "Tile Boost" from the Building Cards.
 * <br/>This Class Represents the Event "Tile Boost", which is a type of Building Card.
 * <br/>The Player gets a SpecialBuff for its Tile, which can be used or ignored.
 */
public class TileBoost implements IF_Event {
    private final SpecialBuff buffType;

    /**
     * Constructor for the Building Event "Tile Boost".
     * <br/><strong>Pre:</strong> buffType != null
     * <br/><strong>Post:</strong> this.buffType = buffType
     *
     * @param buffType Type of SpecialBuff which acts as the boost.
     */
    public TileBoost(SpecialBuff buffType) {
        this.buffType = buffType;
    }

    /**
     * Handles the Building Event "Tile Boost".
     * <br/>This method handles the Building Event "Tile Boost" for the given Player, updating its parameters.
     * <br/><strong>Pre:</strong> player != null && board.players.contains(player)
     * <br/><strong>Post:</strong> player.getSpecialBuffs().add(buffType)
     *
     * @param player Player to update due to the Event.
     */
    @Override
    public void handleEvent(Player player) {
        player.updateStats(buffType);
    }

    /**
     * @see IF_Event Tile Boost implementation of the getAttributes method.
     */
    @Override
    public void getAttributes(StringBuilder str1, StringBuilder str2, StringBuilder str3) {
        str1.append("Additional");
        str3.append(buffToStr());
    }

    // convert buffType to the right string
    private String buffToStr() {
        return switch (buffType) {
            case ADDITIONAL_FOOD_TILE -> " Food";
            case ADDITIONAL_UP_TILE -> " UpMove";
        };
    }

    /**
     * @see IF_Event Tile Boost implementation of the getInfo method.
     */
    @Override
    public String getInfo(StringBuilder info) {
         info.append("This Building grants its owner 1 more ");
        return switch (buffType) {
            case ADDITIONAL_FOOD_TILE -> info.append("food if his totem is then placed on a food tile.").toString();
            case ADDITIONAL_UP_TILE -> info.append("UpMove at the end of the round.").toString();
        };
    }

    /**
     * @see IF_Event Tile Boost implementation of the getArt method.
     */
    @Override
    public String getArt(){
        return "tb";
    }
}
