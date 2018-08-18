package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ServiceGenerator;

import java.io.Serializable;
import java.util.Date;

public class Votes {

    private final static String API_ID = "id";
    private final static String API_KIND = "kind";
    private final static String API_CREATED_AT = "created_at";
    private final static String API_MESSAGE = "message";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_KIND)
    public Kind kind;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_MESSAGE)
    public Messages message;

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }

    public enum Kind implements Serializable {
        @SerializedName("upvote") UPVOTE, @SerializedName("downvote") DOWNVOTE, @SerializedName("trollvote") TROLLVOTE, @SerializedName("problem") PROBLEM;

        public String toString() {
            return name().toLowerCase();
        }
    }
}
