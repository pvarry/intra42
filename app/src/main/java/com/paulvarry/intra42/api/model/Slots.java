package com.paulvarry.intra42.api.model;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ServiceGenerator;

import java.lang.reflect.Type;
import java.util.Date;

public class Slots {

    private static final String API_ID = "id";
    private static final String API_BEGIN_AT = "begin_at";
    private static final String API_END_AT = "end_at";
    private static final String API_SCALE_TEAM = "scale_team";
    private static final String API_USER = "user";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @SerializedName(API_END_AT)
    public Date endAt;
    @SerializedName(API_SCALE_TEAM)
    public ScaleTeams scaleTeam;
    @SerializedName(API_USER)
    public UsersLTE user;

    public boolean isBooked;

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }

    static public class SlotsDeserializer implements JsonDeserializer<Slots> {

        @Override
        public Slots deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            Slots slots = new Slots();
            JsonObject jsonObject = json.getAsJsonObject();
            Gson gson = ServiceGenerator.getGson();

            slots.id = jsonObject.get(API_ID).getAsInt();
            slots.beginAt = gson.fromJson(jsonObject.get(API_BEGIN_AT), Date.class);
            slots.endAt = gson.fromJson(jsonObject.get(API_END_AT), Date.class);

            JsonElement elementScaleTeam = jsonObject.get(API_SCALE_TEAM);
            if (elementScaleTeam != null && elementScaleTeam.isJsonObject()) { // when is booked and can't be see by user, value is "invisible"
                slots.scaleTeam = gson.fromJson(elementScaleTeam, ScaleTeams.class);
                slots.isBooked = true;
            } else
                slots.isBooked = false;
            JsonElement elementUser = jsonObject.get(API_USER);
            if (elementUser != null && elementUser.isJsonObject())
                slots.user = gson.fromJson(elementUser, UsersLTE.class);

            return slots;
        }
    }

    static public class SlotsSerializer implements JsonSerializer<Slots> {

        @Override
        public JsonElement serialize(Slots src, Type typeOfSrc, JsonSerializationContext context) {

            Gson gson = ServiceGenerator.getGson();

            JsonObject object = new JsonObject();
            object.addProperty(API_ID, src.id);
            object.add(API_BEGIN_AT, gson.toJsonTree(src.beginAt));
            object.add(API_END_AT, gson.toJsonTree(src.endAt));
            if (src.isBooked && src.scaleTeam == null)
                object.addProperty(API_SCALE_TEAM, "invisible");
            else
                object.add(API_SCALE_TEAM, gson.toJsonTree(src.scaleTeam));

            object.add(API_USER, gson.toJsonTree(src.user));
            return object;
        }
    }
}
