package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.ServiceGenerator;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

/**
 * A scale done (or doing) by a team.
 */
public class ScaleTeams {

    private static final String API_ID = "id";
    private static final String API_SCALE_ID = "scale_id";
    private static final String API_COMMENT = "comment";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPDATED_AT = "updated_at";
    private static final String API_FEEDBACK = "feedback";
    private static final String API_FEEDBACK_RATING = "feedback_rating";
    private static final String API_FINAL_MARK = "final_mark";
    private static final String API_FLAG = "flag";
    private static final String API_BEGIN_AT = "begin_at";
    private static final String API_CORRECTEDS = "correcteds";
    private static final String API_CORRECTOR = "corrector";
    private static final String API_TRUANT = "truant";
    private static final String API_SCALE = "scale";
    private static final String API_TEAM = "team";
    private static final String API_FEEDBACKS = "feedbacks";
    private static final String API_FILLED_AT = "filled_at";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_SCALE_ID)
    public int scaleId;
    @Nullable
    @SerializedName(API_COMMENT)
    public String comment;
    @SerializedName(API_CREATED_AT)
    public Date created_at;
    @SerializedName(API_UPDATED_AT)
    public Date updated_at;
    @Nullable
    @SerializedName(API_FEEDBACK)
    public String feedback;
    @Nullable
    @SerializedName(API_FEEDBACK_RATING)
    public int feedback_rating;
    @Nullable
    @SerializedName(API_FINAL_MARK)
    public Integer finalMark;
    @SerializedName(API_FLAG)
    public ScaleTeamsFlag flag;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @Nullable
    @SerializedName(API_CORRECTEDS)
    public List<UsersLTE> correcteds;
    @Nullable
    @SerializedName(API_CORRECTOR)
    public UsersLTE corrector;
    @SerializedName(API_TRUANT)
    public UsersLTE truant;
    @Nullable
    @SerializedName(API_SCALE)
    public Scale scale;
    @Nullable
    @SerializedName(API_TEAM)
    public Teams teams;
    @SerializedName(API_FEEDBACKS)
    public List<Feedback> feedbacks;
    @SerializedName(API_FILLED_AT)
    public Date filledAt;

    public boolean equals(ScaleTeams scaleTeams) {
        return scaleTeams.id == id;
    }

    static public class ScaleTeamsDeserializer implements JsonDeserializer<ScaleTeams> {

        @Override
        public ScaleTeams deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            ScaleTeams teams = new ScaleTeams();
            JsonObject jsonObject = json.getAsJsonObject();
            Gson gson = ServiceGenerator.getGson();

            if (!jsonObject.get(API_CORRECTEDS).isJsonNull() &&
                    !jsonObject.get(API_CORRECTEDS).isJsonArray() &&
                    jsonObject.get(API_CORRECTEDS).getAsString().equals("invisible"))
                jsonObject.add(API_CORRECTEDS, null);

//            teams.id = jsonObject.get(API_ID).getAsInt();
//            teams.scaleId = gson.fromJson(jsonObject.get(API_SCALE_ID), int.class);
//            teams.comment = gson.fromJson(jsonObject.get(API_COMMENT), String.class);
//            teams.created_at = gson.fromJson(jsonObject.get(API_CREATED_AT), Date.class);
//            teams.updated_at = gson.fromJson(jsonObject.get(API_UPDATED_AT), Date.class);
//            teams.feedback = gson.fromJson(jsonObject.get(API_FEEDBACK), String.class);
//            teams.feedback_rating = gson.fromJson(jsonObject.get(API_FEEDBACK_RATING), String.class);
//            teams.finalMark = gson.fromJson(jsonObject.get(API_FINAL_MARK), Integer.class);
//            teams.flag = gson.fromJson(jsonObject.get(API_FLAG), ScaleTeamsFlag.class);
//            teams.begin_at = gson.fromJson(jsonObject.get(API_BEGIN_AT), Date.class);
//            teams.feedback_rating = gson.fromJson(jsonObject.get(API_FEEDBACK_RATING), String.class);

            return teams;
        }
    }

    public static class ScaleTeamsFlag {

        static final String API_ID = "id";
        static final String API_NAME = "name";
        static final String API_POSITIVE = "positive";
        static final String API_ICON = "icon";
        static final String API_CREATED_AT = "created_at";
        static final String API_UPDATED_AT = "updated_at";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_NAME)
        public String name;
        @SerializedName(API_POSITIVE)
        public boolean positive;
        @SerializedName(API_ICON)
        public String icon;
        @SerializedName(API_CREATED_AT)
        public Date createdAt;
        @SerializedName(API_UPDATED_AT)
        public Date updatedAt;

    }

    static public class Feedback {

        private static final String API_ID = "id";
        private static final String API_SCALE_TEAM_ID = "scale_team_id";
        private static final String API_RATE = "rate";
        private static final String API_KIND = "kind";
        private static final String API_CREATED_AT = "created_at";
        private static final String API_UPDATED_AT = "updated_at";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_SCALE_TEAM_ID)
        public int scaleTeamId;
        @SerializedName(API_RATE)
        public int rate;
        @SerializedName(API_KIND)
        public Kind kind;
        @SerializedName(API_CREATED_AT)
        public Date createdAt;
        @SerializedName(API_UPDATED_AT)
        public Date updatedAt;

        public enum Kind {
            @SerializedName("nice")NICE,
            @SerializedName("rigorous")RIGOROUS,
            @SerializedName("interested")INTERESTED,
            @SerializedName("punctuality")PUNCTUALITY
        }

    }

    public static class Scale {

        private static final String API_ID = "id";
        private static final String API_EVALUATION_ID = "evaluation_id";
        private static final String API_NAME = "name";
        private static final String API_IS_PRIMARY = "is_primary";
        private static final String API_COMMENT = "comment";
        private static final String API_INTRODUCTION_MD = "introduction_md";
        private static final String API_DISCLAIMER_MD = "disclaimer_md";
        private static final String API_CREATED_AT = "created_at";
        private static final String API_CORRECTION_NUMBER = "correction_number";
        private static final String API_DURATION = "duration";
        private static final String API_MANUAL_SUBSCRIPTION = "manual_subscription";
        private static final String API_LANGUAGES = "languages";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_EVALUATION_ID)
        public int evaluationId;
        @SerializedName(API_NAME)
        public String name;
        @SerializedName(API_IS_PRIMARY)
        public boolean isPrimary;
        @SerializedName(API_COMMENT)
        public String comment;
        @SerializedName(API_INTRODUCTION_MD)
        public String introductionMd;
        @SerializedName(API_DISCLAIMER_MD)
        public String disclaimerMd;
        @SerializedName(API_CREATED_AT)
        public Date createdAt;
        @SerializedName(API_CORRECTION_NUMBER)
        public int correctionNumber;
        @SerializedName(API_DURATION)
        public int duration;
        @SerializedName(API_MANUAL_SUBSCRIPTION)
        public boolean manualSubscription;
        @SerializedName(API_LANGUAGES)
        public List<Language> languages;

    }

}