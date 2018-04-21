package com.paulvarry.intra42.api.model;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.IBaseItem;

import java.util.Date;
import java.util.List;

public class Events implements IBaseItem {

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

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public String getSub(Context context) {
        return location;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }

    public enum EventKind {
        @SerializedName("conference") CONFERENCE(R.string.event_kind_conf, R.color.tag_event_conference),
        @SerializedName("meet_up") MEET_UP(R.string.event_kind_meet_up, R.color.tag_event_meet_up),
        @SerializedName("extern") EXTERN(R.string.event_kind_extern, R.color.tag_event_extern),
        @SerializedName("hackathon") HACKATHON(R.string.event_kind_hackathon, R.color.tag_event_hackathon),
        @SerializedName("workshop") WORKSHOP(R.string.event_kind_workshop, R.color.tag_event_workshop),
        @SerializedName("event") EVENT(R.string.event_kind_event, R.color.tag_event_event),
        @SerializedName("atelier") ATELIER(R.string.event_kind_atelier, R.color.tag_event_atelier),
        @SerializedName("other") OTHER(R.string.event_kind_other, R.color.tag_event_other),
        @SerializedName("association") ASSOCIATION(R.string.event_kind_association, R.color.tag_event_association),
        @SerializedName("partnership") PARTNERSHIP(R.string.event_kind_partnership, R.color.tag_event_partnership),
        @SerializedName("challenge") CHALLENGE(R.string.event_kind_challenge, R.color.tag_event_challenge);

        @StringRes
        private final int name;
        @ColorRes
        private final int color;

        EventKind(@StringRes int name, @ColorRes int color) {
            this.name = name;
            this.color = color;
        }

        @StringRes
        public int getName() {
            return name;
        }

        @ColorRes
        public int getColorRes() {
            return color;
        }

        @ColorInt
        public int getColorInt(Context context) {
            return context.getResources().getColor(color);
        }

        public String getString(Context context) {
            return context.getString(name);
        }
    }

}
