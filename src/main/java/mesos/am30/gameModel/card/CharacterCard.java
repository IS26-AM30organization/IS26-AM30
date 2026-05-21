package mesos.am30.gameModel.card;

import mesos.am30.common.TColors;
import mesos.am30.gameModel.Parameter;
import mesos.am30.gameModel.board.Board;

public class CharacterCard extends Card {
    private final Parameter role;
    private final Integer value;
    private final Integer prestigePoints;

    public CharacterCard(int era, Parameter role, Integer value, Integer prestigePoints, int id) {
        super(era, id);
        this.role = role;
        this.value = value;
        this.prestigePoints = prestigePoints;
    }

    public Parameter getRole() {
        return role;
    }

    public Integer getValue() {
        return value;
    }

    public Integer getPrestigePoints() {
        return prestigePoints;
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    public void drawUp(Board board){
        board.drawUp(this);
    }

    public void drawDown(Board board){
        board.drawDown(this);
    }

    public void discard(Board board){
        board.discard(this);
    }

    @Override
    public void createRow(StringBuilder rowRoles, StringBuilder rowValue, StringBuilder rowPP) {
        String r = role + " " + "\u265F";
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
        for (int x = TColors.getVisibleLength(r); x < maxWidth; x++) rowRoles.append(" ");

        rowValue.append(i);
        for (int x = TColors.getVisibleLength(i); x < maxWidth; x++) rowValue.append(" ");

        rowPP.append(pp);
        for (int x = TColors.getVisibleLength(pp); x < maxWidth; x++) rowPP.append(" ");
    }

    @Override
    public String getArt(){
        return new String(role.name().toLowerCase().charAt(0) + (id % 2 == 0 ? "f" : "m"));
    }

    @Override
    public String getFrame(){
        return new String(prestigePoints+""+role.name().toLowerCase().charAt(0)+""+(value<0 ? value*(-1) : value));
    }

    /**
     * Based on the card's role, the item must be specified to be displayed on terminal
     */
    private void valueToItem(StringBuilder str1) {
        switch (role) {
            case INVENTOR -> str1.append(TColors.DARK_GRAY).append("Invention:").append(TColors.RESET);
            case BUILDER, GATHERER, HUNTER -> str1.append(TColors.PINK).append("Food:").append(TColors.RESET);
            case SHAMAN -> str1.append(TColors.GOLD).append("Stars:").append(TColors.RESET);
            case ARTIST -> {}
            default -> str1.append("ITEM:");
        }
    }

    @Override
    public String getCardInfo(StringBuilder info) {
        return info.append("Character")
                .toString();
    }
}