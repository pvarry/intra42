package com.paulvarry.intra42.api.model;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.BaseItem;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.cache.BaseCacheData;

import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class UsersLTE
        extends BaseCacheData
        implements BaseItem, Comparable<UsersLTE> {

    final static String API_ID = "id";
    final static String API_LOGIN = "login";
    final static String API_URL = "url";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_LOGIN)
    public String login;
    @SerializedName(API_URL)
    public String url;

    public static Type getListType() {
        return new TypeToken<List<UsersLTE>>() {
        }.getType();
    }

    public boolean equals(UsersLTE user) {
        return user != null && user.id == id;
    }

    public boolean isMe(AppClass app) {
        return equals(app.me);
    }

    @Override
    public String getName(Context context) {
        return login;
    }

    @Override
    public String getSub(Context context) {
        return null;
    }

    @Override
    public boolean openIt(Context context) {
        UserActivity.openIt(context, this);
        return true;
    }

    @Override
    public int compareTo(@NonNull UsersLTE o) {
        return login.compareTo(o.login);
    }

    static public class UserLTEDeserializer implements JsonDeserializer<UsersLTE> {

        @Override
        public UsersLTE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject() || json.isJsonNull())
                return null;

            UsersLTE user = new UsersLTE();
            JsonObject jsonObject = json.getAsJsonObject();
            Gson gson = ServiceGenerator.getGson();

            if (jsonObject.size() == 0)
                return null;

            user.id = jsonObject.get(API_ID).getAsInt();
            user.login = gson.fromJson(jsonObject.get(API_LOGIN), String.class);
            user.url = gson.fromJson(jsonObject.get(API_URL), String.class);

            return user;
        }
    }

    static public class ListUserLTEDeserializer implements JsonDeserializer<List<UsersLTE>> {

        @Override
        public List<UsersLTE> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonArray() || json.isJsonNull())
                return null;

            List<UsersLTE> list = new ArrayList<>();
            JsonArray jsonObject = json.getAsJsonArray();
            Gson gson = ServiceGenerator.getGson();

            for (int i = 0; i < jsonObject.size(); i++) {
                list.add(gson.fromJson(jsonObject.get(i), UsersLTE.class));
            }

            return list;
        }
    }
}
