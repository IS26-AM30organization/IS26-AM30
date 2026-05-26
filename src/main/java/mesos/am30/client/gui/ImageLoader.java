package mesos.am30.client.gui;

import javafx.scene.image.Image;
import mesos.am30.gameModel.Player;
import mesos.am30.gameModel.card.Card;

import java.util.HashMap;

public class ImageLoader {
    private static HashMap<String, Image> arts = new HashMap<>();
    private static HashMap<String, Image> frames = new HashMap<>();
    private static HashMap<String, Image> totems = new HashMap<>();

    public static Image loadArt(Card card){
        String art = card.getArt();
        System.out.println(art);
        if (!arts.containsKey(art))
            arts.put(art,new Image(ImageLoader.class.getResource("/images/"+ art + ".png").toExternalForm()));

        return arts.get(art);
    }

    public static Image loadArt(Player p, int i){
        String art = "/icons/player"+(i+1)+".png";
        System.out.println(art);
        if (!totems.containsKey(art))
            totems.put(art,new Image(ImageLoader.class.getResource(art).toExternalForm()));

        return totems.get(art);
    }

    public static Image loadFrame(Card card){
        String frame = card.getFrame();
        System.out.println(frame);
        if(!frames.containsKey(frame))
            frames.put(frame, new Image(ImageLoader.class.getResource("/images/"+ frame + ".png").toExternalForm()));

        return frames.get(frame);
    }
}
