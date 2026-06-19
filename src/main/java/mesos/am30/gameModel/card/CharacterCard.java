package mesos.am30.gameModel.card;

import mesos.am30.common.TColors;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.board.Board;

/**
 * Representation of a Character Card.
 * <br/>This Class works as the representation for a Character Card, independently of the role of the Character.
 */
public class CharacterCard extends Card {
    private final Parameter role;
    private final Integer value;
    private final Integer prestigePoints;

    /**
     * Constructor of a Character Card.
     * <br/><strong>Pre:</strong> 1 &lt;= era &lt;= 4 &amp;&amp; id > 1 &amp;&amp; role != null &amp;&amp; value != null &amp;&amp; prestigePoints != null
     * <br/><strong>Post:</strong> this.era = era &amp;&amp; this.id = id &amp;&amp; this.role = role &amp;&amp; this.value = value &amp;&amp; this.prestigePoints = prestigePoints
     *
     * @param era            Era when the Card is draw.
     * @param id             Unique ID of the Card.
     * @param role           Role of the Character.
     * @param value          Value of the Character attribute (if existing).
     * @param prestigePoints Value of the Character in prestige points (if existing).
     */
    public CharacterCard(int era, int id, Parameter role, Integer value, Integer prestigePoints) {
        super(era, id);
        this.role = role;
        this.value = value;
        this.prestigePoints = prestigePoints;
    }

    /**
     * Getter for the attribute "role".
     *
     * @return Role of the Character.
     */
    public Parameter getRole() {
        return role;
    }

    /**
     * Getter for the attribute "value".
     *
     * @return Value of the Character attribute (if existing).
     */
    public Integer getValue() {
        return value;
    }

    /**
     * Getter for the attribute "prestigePoints".
     *
     * @return Value of the Character in prestige points (if existing).
     */
    public Integer getPrestigePoints() {
        return prestigePoints;
    }

    /**
     * @see Card Character Card implementation of the isPickable method.
     */
    @Override
    public boolean isPickable() {
        return true;
    }

    /**
     * @see Card Character Card implementation of the drawUp method.
     */
    @Override
    public void drawUp(Board board) {
        board.drawUp(this);
    }

    /**
     * @see Card Character Card implementation of the drawDown method.
     */
    @Override
    public void drawDown(Board board) {
        board.drawDown(this);
    }

    /**
     * @see Card Character Card implementation of the discard method.
     */
    @Override
    public void discard(Board board) {
        board.discard(this);
    }

    /**
     * @see Card Character Card implementation of the createRow method.
     */
    @Override
    public void createRow(StringBuilder rowRoles, StringBuilder rowValue, StringBuilder rowPP) {
        String r = role + " " + "♟";
        StringBuilder i = new StringBuilder();

        if (value != 0) {
            valueToItem(i);
            i.append(value);
        }
        String pp = "";
        if (prestigePoints != 0) pp = "PP:" + prestigePoints;

        //need to take the longest word
        int maxWidth = TColors.getVisibleLength(r);
        if (TColors.getVisibleLength(i) > maxWidth) {
            maxWidth = TColors.getVisibleLength(i);
        }
        if (TColors.getVisibleLength(pp) > maxWidth) {
            maxWidth = TColors.getVisibleLength(pp);
        }
        maxWidth += 5;

        rowRoles.append(TColors.SILVER_B).append(r).append(TColors.RESET);
        rowRoles.repeat(" ", Math.max(0, maxWidth - TColors.getVisibleLength(r)));

        rowValue.append(i);
        rowValue.repeat(" ", Math.max(0, maxWidth - TColors.getVisibleLength(i)));

        rowPP.append(pp);
        rowPP.repeat(" ", Math.max(0, maxWidth - TColors.getVisibleLength(pp)));
    }

    // Display the correct value to the Terminal
    private void valueToItem(StringBuilder stringBuilder) {
        switch (role) {
            case INVENTOR -> stringBuilder.append(TColors.DARK_GRAY).append("Invention:").append(TColors.RESET);
            case BUILDER, GATHERER, HUNTER -> stringBuilder.append(TColors.PINK).append("Food:").append(TColors.RESET);
            case SHAMAN -> stringBuilder.append(TColors.GOLD).append("Stars:").append(TColors.RESET);
            default -> stringBuilder.append("ITEM:");
        }
    }

    /**
     * @see Card Character Card implementation of the getCardInfo method.
     */
    @Override
    public String getCardInfo(StringBuilder info) {
        return info.append("Character")
                .toString();
    }

    /**
     * @see Card Character Card implementation of the getArt method.
     */
    @Override
    public String getArt() {
        return role.name().toLowerCase().charAt(0) + (id % 2 == 0 ? "f" : "m");
    }

    /**
     * @see Card Character Card implementation of the getFrame method.
     */
    @Override
    public String getFrame() {
        return prestigePoints + "" + role.name().toLowerCase().charAt(0) + (value < 0 ? value * (-1) : value);
    }
}