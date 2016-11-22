package com.paulvarry.intra42.api;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.firebase.database.DatabaseReference;
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
import com.paulvarry.intra42.BaseCacheData;
import com.paulvarry.intra42.BaseItem;
import com.paulvarry.intra42.oauth.ServiceGenerator;
import com.paulvarry.intra42.tab.user.UserActivity;

import org.parceler.Parcel;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Parcel
public class UserLTE
        extends BaseCacheData
        implements BaseItem {

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
        return new TypeToken<List<UserLTE>>() {
        }.getType();
    }

    public boolean equals(UserLTE user) {
        return user != null && user.id == id;
    }

    public boolean isMe(AppClass app) {
        return equals(app.me);
    }

    @Override
    public String getName() {
        return login;
    }

    @Override
    public String getSub() {
        return null;
    }

    @Override
    public boolean openIt(Context context) {
        UserActivity.openIt(context, this);
        return true;
    }

    @Nullable
    public DatabaseReference getFriendsFirebaseRef(AppClass app) {
        if (app.firebaseRefFriends != null)
            return app.firebaseRefFriends.child(String.valueOf(this.id));
        return null;
    }

    static public class UserLTEDeserializer implements JsonDeserializer<UserLTE> {

        @Override
        public UserLTE deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonObject() || json.isJsonNull())
                return null;

            UserLTE user = new UserLTE();
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

    static public class ListUserLTEDeserializer implements JsonDeserializer<List<UserLTE>> {

        @Override
        public List<UserLTE> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (!json.isJsonArray() || json.isJsonNull())
                return null;

            List<UserLTE> list = new ArrayList<>();
            JsonArray jsonObject = json.getAsJsonArray();
            Gson gson = ServiceGenerator.getGson();

            for (int i = 0; i < jsonObject.size(); i++) {
                list.add(gson.fromJson(jsonObject.get(i), UserLTE.class));
            }

            return list;
        }
    }
}
