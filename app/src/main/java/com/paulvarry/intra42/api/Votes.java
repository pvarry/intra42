package com.paulvarry.intra42.api;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.oauth.ServiceGenerator;

import java.util.Date;

public class Votes {

    public final static String KIND_UP = "upvote";
    public final static String KIND_DOWN = "downvote";
    public final static String KIND_TROLL = "trollvote";
    public final static String KIND_PROBLEM = "problem";

    private final static String API_ID = "id";
    private final static String API_KIND = "kind";
    private final static String API_CREATED_AT = "created_at";
    private final static String API_MESSAGE = "message";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_KIND)
    public String kind;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_MESSAGE)
    public Messages message;

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }
}
