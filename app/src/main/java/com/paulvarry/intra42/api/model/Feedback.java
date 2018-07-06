package com.paulvarry.intra42.api.model;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class Feedback {

    private static final String API_ID = "id";
    private static final String API_USER = "user";
    private static final String API_SCALE_FEEDBACKABLE_TYPE = "feedbackable_type";
    private static final String API_SCALE_FEEDBACKABLE_ID = "feedbackable_id";
    private static final String API_SCALE_COMMENT = "comment";
    private static final String API_RATING = "rating";
    private static final String API_CREATED_AT = "created_at";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_USER)
    public UsersLTE user;
    @SerializedName(API_SCALE_FEEDBACKABLE_TYPE)
    public String feedbackable_type;
    @SerializedName(API_SCALE_FEEDBACKABLE_ID)
    public int feedbackable_id;
    @SerializedName(API_SCALE_COMMENT)
    public String comment;
    @SerializedName(API_RATING)
    public int rating;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName("feedback_details")
    public List<FeedbackDetails> feedbackDetails;

    static public class FeedbackDetails {

        @SerializedName("id")
        public int id;
        @SerializedName("rate")
        public int rate;
        @SerializedName("kind")
        public Kind kind;

        public enum Kind {
            @SerializedName("nice") NICE,
            @SerializedName("rigorous") RIGOROUS,
            @SerializedName("interested") INTERESTED,
            @SerializedName("punctuality") PUNCTUALITY
        }
    }
}
