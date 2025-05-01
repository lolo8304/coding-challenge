package timezone;
import com.google.gson.*;
import java.lang.reflect.Type;

public class TimezoneAbbrJsonAdapter implements JsonSerializer<TimezoneAbbr>, JsonDeserializer<TimezoneAbbr> {

    @Override
    public JsonElement serialize(TimezoneAbbr src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        var countryArray = new JsonArray();
        for (var country : src.countryCodes().split(",")) {
            countryArray.add(country.trim());
        }
        jsonObject.add("countryCodes", countryArray);
        jsonObject.addProperty("tzIdentifier", src.tzIdentifier());
        jsonObject.addProperty("comments", src.comments());
        jsonObject.addProperty("type", src.type());
        jsonObject.addProperty("utcOffsetSdt", src.utcOffsetSdt());
        jsonObject.addProperty("utcOffsetDst", src.utcOffsetDst());
        jsonObject.addProperty("timezoneSdt", src.timezoneSdt());
        jsonObject.addProperty("timezoneDst", src.timezoneDst());
        jsonObject.addProperty("source", src.source());
        jsonObject.addProperty("notes", src.notes());
        jsonObject.addProperty("isAlias", src.isAlias());
        return jsonObject;
    }

    @Override
    public TimezoneAbbr deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        return new TimezoneAbbr(
            jsonObject.get("countryCodes").getAsString(),
            jsonObject.get("tzIdentifier").getAsString(),
            jsonObject.get("comments").getAsString(),
            jsonObject.get("type").getAsString(),
            jsonObject.get("utcOffsetSdt").getAsString(),
            jsonObject.get("utcOffsetDst").getAsString(),
            jsonObject.get("timezoneSdt").getAsString(),
            jsonObject.get("timezoneDst").getAsString(),
            jsonObject.get("source").getAsString(),
            jsonObject.get("notes").getAsString(),
            jsonObject.get("isAlias").getAsBoolean()
        );
    }
}