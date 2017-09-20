package com.paulvarry.intra42.api.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.TopicActivity;
import com.paulvarry.intra42.api.BaseItem;
import com.paulvarry.intra42.api.ServiceGenerator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Topics implements BaseItem {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_AUTHOR = "author";
    private static final String API_KIND = "kind";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPDATED_AT = "updated_at";
    private static final String API_PINNED_AT = "pinned_at";
    private static final String API_LOCKED_AT = "locked_at";
    private static final String API_PINNER = "pinner";
    private static final String API_LOCKER = "locker";
    private static final String API_LANGUAGE = "language";
    private static final String API_MESSAGES_URL = "messages_url";
    private static final String API_MESSAGE = "message";
    private static final String API_TAGS = "tags";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_AUTHOR)
    public UsersLTE author;
    @SerializedName(API_KIND)
    public String kind;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPDATED_AT)
    public Date updatedAt;
    @SerializedName(API_PINNED_AT)
    public Date pinnedAt;
    @SerializedName(API_LOCKED_AT)
    public Date lockedAt;
    @SerializedName(API_PINNER)
    public UsersLTE pinner;
    @SerializedName(API_LOCKER)
    public UsersLTE locker;
    @SerializedName(API_LANGUAGE)
    public Language language;
    @SerializedName(API_MESSAGES_URL)
    public String messagesUrl;
    @SerializedName(API_MESSAGE)
    public Message message;
    @SerializedName(API_TAGS)
    public List<Tags> tags;

    @Override
    public String toString() {
        return ServiceGenerator.getGson().toJson(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSub() {
        return author.login;
    }

    @Override
    public boolean openIt(Context context) {
        TopicActivity.openIt(context, this);
        return true;
    }

    public static class Message {

        static final String API_ID = "id";
        static final String API_MESSAGE = "content";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_MESSAGE)
        public Content content;

        static public class Content {

            static final String API_MARKDOWN = "markdown";
            static final String API_HTML = "html";

            @SerializedName(API_MARKDOWN)
            public String markdown;
            @SerializedName(API_HTML)
            public String html;
        }
    }

    public static class Kind {

        public String slug;
        public String name;

        public Kind(String slug, String name) {
            this.slug = slug;
            this.name = name;
        }

        public static List<Kind> getListOfKind(Context context) {

            List<Kind> list = new ArrayList<>();

            list.add(new Kind("annonce", context.getString(R.string.forum_topics_kind_announce)));
            list.add(new Kind("survey", context.getString(R.string.topics_kind_survey)));
            list.add(new Kind("question", context.getString(R.string.forum_topics_kind_question)));

            return list;

        }
    }
}
