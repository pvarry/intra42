package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;

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
    public UserLTE user;
    @SerializedName(API_EVENT)
    public Events event;

}
