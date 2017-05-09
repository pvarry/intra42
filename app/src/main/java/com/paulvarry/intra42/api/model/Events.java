package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Events {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_DESCRIPTION = "description";
    private static final String API_LOCATION = "location";
    private static final String API_KIND = "kind";
    private static final String API_MAX_PEOPLE = "max_people";
    private static final String API_NBR_SUBSCRIBERS = "nbr_subscribers";
    private static final String API_BEGIN_AT = "begin_at";
    private static final String API_END_AT = "end_at";
    private static final String API_CAMPUS_IDS = "campus_ids";
    private static final String API_CURSUS_IDS = "cursus_ids";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_DESCRIPTION)
    public String description;
    @SerializedName(API_LOCATION)
    public String location;
    @Nullable
    @SerializedName(API_KIND)
    public EventKind kind;
    @SerializedName(API_MAX_PEOPLE)
    public int maxPeople;
    @SerializedName(API_NBR_SUBSCRIBERS)
    public int nbrSubscribers;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @SerializedName(API_END_AT)
    public Date endAt;
    @SerializedName(API_CAMPUS_IDS)
    public List<Integer> campus;
    @SerializedName(API_CURSUS_IDS)
    public List<Integer> cursus;

    public enum EventKind {
        @SerializedName("conference")CONFERENCE,
        @SerializedName("meet_up")MEET_UP,
        @SerializedName("extern")EXTERN,
        @SerializedName("hackathon")HACKATHON,
        @SerializedName("workshop")WORKSHOP,
        @SerializedName("event")EVENT,
        @SerializedName("atelier")ATELIER,
        @SerializedName("other")OTHER,
        @SerializedName("association")ASSOCIATION
    }

}
