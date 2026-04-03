package mesos.am30.GameModel;

import com.google.gson.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Utility {

    public static <T> List<T> cardLoader(String fileName, int playerNum, Type cardType) throws IOException {
        List<T> extractedList = new ArrayList<>();
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(IF_Event.class, new EventDeserializer())
                .create();

        InputStream fileStream = Utility.class.getClassLoader().getResourceAsStream(fileName);
        Reader reader = new InputStreamReader(fileStream, UTF_8); //might want a try-catch

        JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();
        //JSON is now an array of generics objects

        for (JsonElement element : jsonArray) {
            JsonObject obj = element.getAsJsonObject();

            int minRequired = obj.get("playersMinimum").getAsInt();

            if (playerNum >= minRequired) {
                T item = gson.fromJson(obj, cardType);
                extractedList.add(item);
            }
        }
        reader.close();
        fileStream.close();
        return extractedList;
    }
}
/*
https://javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/JsonElement.html
https://javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/JsonParser.html
https://javadoc.io/doc/com.google.code.gson/gson/latest/com.google.gson/com/google/gson/JsonObject.html
*/

/* RuntimeTypeAdapterFactory:
https://stackoverflow.com/questions/16396904/using-gson-with-interface-types
https://medium.com/sanoma-technology-blog/gson-using-autovalue-and-polymorphism-787452ac60ca
 */
