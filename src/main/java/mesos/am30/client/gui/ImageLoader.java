package mesos.am30.client.gui;

import javafx.scene.image.Image;
import mesos.am30.gameModel.card.Card;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;

/**
 * Utility for loading and caching JavaFX images from application resources.
 * <br/>Images are loaded on first access and cached to avoid redundant I/O.
 */
public class ImageLoader {
    private static final HashMap<String, Image> arts = new HashMap<>();
    private static final HashMap<String, Image> frames = new HashMap<>();
    private static final HashMap<String, Image> totems = new HashMap<>();

    /**
     * Loads and caches the art image for the given card.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card The card whose art image to load.
     * @return The card's art image.
     */
    public static Image loadArt(Card card) {
        String art = card.getArt();
        if (!arts.containsKey(art)) {
            InputStream stream = ImageLoader.class.getResourceAsStream("/images/" + art + ".png");

            if (stream != null) {
                arts.put(art, new Image(stream));
            } else {
                InputStream defaultStream = Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/default.png"));
                arts.put(art, new Image(defaultStream));
            }
        }

        return arts.get(art);
    }

    /**
     * Loads and caches the totem image for the player at the given index.
     *
     * @param i The player's index (0-based), used to select the icon.
     * @return The player's totem image.
     */
    public static Image loadArt(int i) {
        String art = "/icons/player" + (i + 1) + ".png";
        if (!totems.containsKey(art))
            totems.put(art, new Image(Objects.requireNonNull(ImageLoader.class.getResourceAsStream(art))));

        return totems.get(art);
    }

    /**
     * Loads and caches the frame image for the given card.
     * <br/><strong>Pre:</strong> card != null
     *
     * @param card The card whose frame image to load.
     * @return The card's frame image.
     */
    public static Image loadFrame(Card card) {
        String frame = card.getFrame();
        if (!frames.containsKey(frame)) {
            InputStream stream = ImageLoader.class.getResourceAsStream("/images/" + frame + ".png");

            if (stream != null) {
                frames.put(frame, new Image(stream));
            } else {
                InputStream defaultStream = Objects.requireNonNull(ImageLoader.class.getResourceAsStream("/images/null.png"));
                frames.put(frame, new Image(defaultStream));
            }
        }

        return frames.get(frame);
    }
}