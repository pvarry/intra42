package com.paulvarry.intra42.api.model;

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

public class Messages {

    private final static String API_ID = "id";
    private final static String API_AUTHOR = "author";
    private final static String API_CONTENT = "content";
    private final static String API_CONTENT_HTML = "content_html";
    private final static String API_REPLIES = "replies";
    private final static String API_CREATED_AT = "created_at";
    private final static String API_UPDATED_AT = "updated_at";
    private final static String API_PARENT_ID = "parent_id";
    private final static String API_IS_ROOT = "is_root";
    private final static String API_MESSAGEABLE_ID = "messageable_id";
    private final static String API_MESSAGEABLE_TYPE = "messageable_type";
    private final static String API_VOTES_COUNT = "votes_count";
    private final static String API_USER_VOTES = "user_votes";
    private final static String API_READINGS = "readings";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_AUTHOR)
    public UsersLTE author;
    @SerializedName(API_CONTENT)
    public String content;
    @SerializedName(API_CONTENT_HTML)
    public String contentHtml;
    @SerializedName(API_REPLIES)
    public List<Messages> replies;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPDATED_AT)
    public Date updatedAt;
    @SerializedName(API_PARENT_ID)
    public int parent;
    @SerializedName(API_IS_ROOT)
    public boolean isRoot;
    @SerializedName(API_VOTES_COUNT)
    public VotesCount votesCount;
    @SerializedName(API_USER_VOTES)
    public UserVotes userVotes;
    @SerializedName(API_READINGS)
    public int readings;

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }

    public static class VotesCount {

        static final String API_UP_VOTE = "upvote";
        static final String API_DOWN_VOTES = "downvote";
        static final String API_TROLL_VOTES = "trollvote";
        static final String API_PROBLEMS = "problem";

        @SerializedName(API_UP_VOTE)
        public int upvote;
        @SerializedName(API_DOWN_VOTES)
        public int downvote;
        @SerializedName(API_TROLL_VOTES)
        public int trollvote;
        @SerializedName(API_PROBLEMS)
        public int problem;
    }

    public static class UserVotes {

        static final String API_UP_VOTE = "upvote";
        static final String API_DOWN_VOTES = "downvote";
        static final String API_TROLL_VOTES = "trollvote";
        static final String API_PROBLEMS = "problem";

        /**
         * Vote ID.
         */
        @SerializedName(API_UP_VOTE)
        public Integer upvote;
        /**
         * Vote ID.
         */
        @SerializedName(API_DOWN_VOTES)
        public Integer downvote;
        /**
         * Vote ID.
         */
        @SerializedName(API_TROLL_VOTES)
        public Integer trollvote;
        /**
         * Vote ID.
         */
        @SerializedName(API_PROBLEMS)
        public Integer problem;

        static public class UserVotesDeserializer implements JsonDeserializer<UserVotes> {

            @Override
            public UserVotes deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                UserVotes userVotes = new UserVotes();
                JsonObject jsonObject = json.getAsJsonObject();

                userVotes.upvote = getVote(jsonObject.get(API_UP_VOTE));
                userVotes.downvote = getVote(jsonObject.get(API_DOWN_VOTES));
                userVotes.trollvote = getVote(jsonObject.get(API_TROLL_VOTES));
                userVotes.problem = getVote(jsonObject.get(API_PROBLEMS));

                return userVotes;
            }

            Integer getVote(JsonElement jsonElement) {

                if (jsonElement == null || jsonElement.isJsonNull())
                    return null;
                else if (jsonElement.getAsJsonPrimitive().isNumber())
                    return jsonElement.getAsNumber().intValue();
                else
                    return null;
            }
        }
    }
}
