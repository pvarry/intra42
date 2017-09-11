package com.paulvarry.intra42.api.model;

import android.util.SparseArray;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.api.ApiService;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import retrofit2.Call;
import retrofit2.Response;

public class EventsUsers {

    private static final String API_ID = "id";
    private static final String API_EVENT_ID = "event_id";
    private static final String API_USER_ID = "user_id";
    private static final String API_USER = "user";
    private static final String API_EVENT = "event";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_EVENT_ID)
    public int eventId;
    @SerializedName(API_USER_ID)
    public int user_id;
    @SerializedName(API_USER)
    public UsersLTE user;
    @SerializedName(API_EVENT)
    public Events event;

    public static SparseArray<EventsUsers> get(AppClass app, ApiService apiService, List<Events> events) throws IOException {

        if (app == null || app.me == null)
            return null;

        String eventsId;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            StringJoiner join = new StringJoiner(",");
            for (Events e : events) {
                join.add(String.valueOf(e.id));
            }
            eventsId = join.toString();
        } else {
            StringBuilder builder = new StringBuilder();
            String join = "";
            for (Events e : events) {
                builder.append(join).append(String.valueOf(e.id));
                join = ",";
            }
            eventsId = builder.toString();
        }

        Call<List<EventsUsers>> callEventsUsers = apiService.getEventsUsers(app.me.id, eventsId);
        Response<List<EventsUsers>> responseEventsUsers = callEventsUsers.execute();

        if (!responseEventsUsers.isSuccessful())
            return null;

        SparseArray<EventsUsers> list = new SparseArray<>();
        for (EventsUsers u : responseEventsUsers.body())
            list.put(u.eventId, u);

        return list;
    }

}
