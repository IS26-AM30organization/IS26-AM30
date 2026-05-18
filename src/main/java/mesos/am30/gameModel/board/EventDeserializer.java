package mesos.am30.gameModel.board;

import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import mesos.am30.gameModel.IF_Event;
import mesos.am30.gameModel.eventIF.*;

import java.lang.reflect.Type;

// Utility JSON Deserializer for the IF_Event events.
class EventDeserializer implements JsonDeserializer<IF_Event> {

    @Override
    public IF_Event deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("eventClass")) {
            throw new JsonParseException("eventClass field missing in JSON");
        }

        String type = jsonObject.get("eventClass").getAsString();

        return switch (type) {
            //context.deserialize(): Delegates mapping to Gson for the identified concrete class
            case "SUSTENANCE"      -> context.deserialize(jsonObject, Sustenance.class);
            case "HUNT"            -> context.deserialize(jsonObject, Hunt.class);
            case "SHAMANIC_RITUAL"  -> context.deserialize(jsonObject, ShamanicRitual.class);
            case "CAVE_PAINTINGS"   -> context.deserialize(jsonObject, CavePaintings.class);

            case "DoubleInventions" -> context.deserialize(jsonObject, DoubleInventions.class);
            case "TileBoost"        -> context.deserialize(jsonObject, TileBoost.class);
            case "OneTimeBoost"     -> context.deserialize(jsonObject, OneTimeBoost.class);
            case "FinalBoost" , "FinalPPBoost" -> context.deserialize(jsonObject, FinalBoost.class);
            case "ShamanBoost"      -> context.deserialize(jsonObject, ShamanBoost.class);
            case "FullSetFinal"     -> context.deserialize(jsonObject, FullSetFinal.class);
            case "FullSet"          -> context.deserialize(jsonObject, FullSet.class);
            case "StatsBoost"       -> context.deserialize(jsonObject, StatsBoost.class);
            case "FinalBuilderBoost" -> context.deserialize(jsonObject, FinalBuilderBoost.class);
            default -> throw new JsonParseException("Unknown Event: " + type);
        };
    }
}
/* Documentation:
https://github.com/google/gson/blob/main/UserGuide.md#writing-a-deserializer
 */