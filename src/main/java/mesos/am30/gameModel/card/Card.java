package mesos.am30.gameModel.card;

import mesos.am30.gameModel.board.Board;

import java.io.Serializable;
import java.util.Objects;

/**
 * Abstract representation of a Card.
 * <br/>This Class works as the base representation for a Card, common to all realizations.
 */
public abstract class Card implements Serializable {
    private final int era;
    final int id;

    /**
     * Constructor of a generic Card.
     * <br/><strong>Pre:</strong> 1 &lt;= era &lt;= 4 &amp;&amp; id > 1
     * <br/><strong>Post:</strong> this.era = era &amp;&amp; this.id = id
     *
     * @param era   Era when the Card is draw.
     * @param id    Unique ID of the Card.
     */
    public Card(int era, int id) {
        this.era = era;
        this.id = id;
    }

    /**
     * Getter for the attribute "id".
     *
     * @return Unique Card ID.
     */
    public int getId(){
        return id;
    }

    /**
     * Getter for the attribute "era".
     *
     * @return Era in which the Card can be drawn.
     */
    public int getEra() {
        return this.era;
    }

    /**
     * Check if the Card is pickable by the Player.
     *
     * @return True if is pickable, false otherwise.
     */
    public boolean isPickable() {
        return false;
    }

    /**
     * Draw a Card and put it in the upper row.
     * <br/>This method is declared abstract since not all Cards can be drawn.
     * <br/><strong>Pre:</strong> board != null
     *
     * @param board Game Board where the Card will be drawn.
     */
    public abstract void drawUp(Board board);

    /**
     * Draw a Card and put it in the lower row.
     * <br/>This method is declared abstract since not all Cards can be drawn.
     * <br/><strong>Pre:</strong> board != null
     *
     * @param board Game Board where the Card will be drawn.
     */
    public abstract void drawDown(Board board);

    /**
     * Discard a Card from any row.
     * <br/>This method works as a default implementation which does nothing, since not all Cards can be discarded the same way.
     * <br/><strong>Pre:</strong> board != null
     *
     * @param board Game Board where the Card will be drawn.
     */
    public abstract void discard(Board board);

    /**
     * Add Card's info to the StringBuilders for the Terminal.
     * <br/>This method works by adding the Card's info to the StringBuilders, in order to display it properly on the Terminal.
     * <br/><strong>Pre:</strong> ln1 != null &amp;&amp; ln2 != null &amp;&amp; ln3 != null
     *
     * @param ln1 Line containing the identifier of the Card.
     * @param ln2 Line containing the main attribute of the Card.
     * @param ln3 Line containing extra attributes of the Card.
     */
    public abstract void createRow(StringBuilder ln1, StringBuilder ln2, StringBuilder ln3);

    /**
     * Get specific information about the Card.
     *
     * @param info Stringbuilder receiving the information.
     *
     * @return Stringbuilder with newly appended information.
     */
    public abstract String getCardInfo(StringBuilder info);

    /**
     * Get the identifier for the Card Art to display on the GUI.
     *
     * @return Card Art identifier.
     */
    public abstract String getArt();

    /**
     * Get the Frame of a Card.
     * <br/>This method is used from the GUI in order to load the correct Frame for each Card.
     * <br/>The default value is an empty String.
     *
     * @return Frame of the Card.
     */
    public String getFrame() {
        return "";
    }

    // Object Methods Override

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return era == card.era && id == card.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(era, id);
    }
}
