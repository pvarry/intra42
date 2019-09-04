package com.paulvarry.intra42.api.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Quests implements Serializable {

    private final static String API_ID = "id";
    private final static String API_NAME = "name";
    private final static String API_SLUG = "slug";
    private final static String API_KIND = "kind";
    private final static String API_INTERNAL_NAME = "internal_name";
    private final static String API_DESCRIPTION = "description";
    private final static String API_CURSUS_ID = "cursus_id";
    private final static String API_CAMPUS_ID = "campus_id";
    private final static String API_GRADE_ID = "grade_id";
    private final static String API_POSITION = "position";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_SLUG)
    public String slug;
    @SerializedName(API_KIND)
    public QuestsKind kind;
    @SerializedName(API_INTERNAL_NAME)
    public String internalName;
    @SerializedName(API_DESCRIPTION)
    public String description;
    @SerializedName(API_CURSUS_ID)
    public Integer cursuId;
    @SerializedName(API_CAMPUS_ID)
    public Integer campusId;
    @Nullable
    @SerializedName(API_GRADE_ID)
    public Integer gradeId;
    @SerializedName(API_POSITION)
    public String position;

    public enum QuestsKind {
        @SerializedName("mandatory")
        MANDATORY,
        @SerializedName("main")
        MAIN
    }

}
