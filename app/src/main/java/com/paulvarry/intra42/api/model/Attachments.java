package com.paulvarry.intra42.api.model;

import androidx.annotation.Nullable;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @see <a href="https://api.intra.42.fr/apidoc/2.0/attachments.html">api.intra.42.fr/apidoc/2.0/attachments</a>
 */

public class Attachments {

    static final String API_ID = "id";
    static final String API_URL = "url";
    static final String API_NAME = "name";
    static final String API_BASE_ID = "base_id";
    static final String API_LANGUAGE = "language";
    static final String API_TYPE = "type";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_URL)
    public String url;
    @SerializedName("urls")
    public Urls urls;
    @SerializedName(API_NAME)
    public String name;
    public int baseId;
    @Nullable
    @SerializedName(API_LANGUAGE)
    public Language language;
    public String type;

    public static class Urls {

        static final String API_URL = "url";
        static final String API_URL_LOW = "low_d";
        static final String API_THUMBS = "thumbs";

        @SerializedName(API_URL)
        public String url;
        @SerializedName(API_URL_LOW)
        public String low_d;
        @SerializedName(API_THUMBS)
        public List<String> thumbs;

    }

    private static class Video extends Attachments {

        static final String API_DURATION = "duration";
        static final String API_URL_LOW = "low_d";
        static final String API_THUMBS = "thumbs";

    }
}
