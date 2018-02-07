package com.paulvarry.intra42.api.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;

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
        @SerializedName("conference")CONFERENCE(R.string.event_kind_conf),
        @SerializedName("meet_up")MEET_UP(R.string.event_kind_meet_up),
        @SerializedName("extern")EXTERN(R.string.event_kind_extern),
        @SerializedName("hackathon")HACKATHON(R.string.event_kind_hackathon),
        @SerializedName("workshop")WORKSHOP(R.string.event_kind_workshop),
        @SerializedName("event")EVENT(R.string.event_kind_event),
        @SerializedName("atelier")ATELIER(R.string.event_kind_atelier),
        @SerializedName("other")OTHER(R.string.event_kind_other),
        @SerializedName("association")ASSOCIATION(R.string.event_kind_association),
        @SerializedName("partnership")PARTNERSHIP(R.string.event_kind_partnership),
        @SerializedName("challenge")CHALLENGE(R.string.event_kind_challenge);

        private final int res;

        EventKind(@StringRes int res) {
            this.res = res;
        }

        public int getRes() {
            return res;
        }

        public String getString(Context context) {
            return context.getString(res);
        }
    }

}
