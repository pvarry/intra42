package com.paulvarry.intra42.api.model;

import androidx.annotation.Nullable;
import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.utils.Pagination;
import com.paulvarry.intra42.utils.Tools;
import retrofit2.Response;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Nullable
    public static List<Slots> getAll(ApiService api) {
        List<Slots> list = new ArrayList<>();
        int i = 1;
        int pageSize = 100;
        int pageMax;

        try {

            Response<List<Slots>> response = api.getSlotsMe(pageSize, Pagination.getPage(list, pageSize)).execute();
            if (!Tools.apiIsSuccessfulNoThrow(response))
                return null;
            list.addAll(response.body());
            int total = Integer.parseInt(response.headers().get("X-Total"));
            pageMax = (int) Math.ceil(total / pageSize);

            while (i <= pageMax) {
                response = api.getSlotsMe(pageSize, Pagination.getPage(list, pageSize)).execute();
                if (!Tools.apiIsSuccessfulNoThrow(response))
                    break;
                list.addAll(response.body());
                ++i;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (list.isEmpty())
            return null;
        return list;
    }

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
            } else // when is booked and can't be see by user, value is "invisible"
                slots.isBooked = elementScaleTeam != null && elementScaleTeam.isJsonPrimitive();
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
