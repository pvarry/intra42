package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

public class CursusUsers {

    private final static String API_ID = "id";
    private final static String API_BEGIN_AT = "begin_at";
    private final static String API_END_AT = "end_at";
    private final static String API_GRADE = "grade";
    private final static String API_LEVEL = "level";
    private final static String API_SKILLS = "skills";
    private final static String API_CURSUS_ID = "cursus_id";
    private final static String API_USER = "user";
    private final static String API_CURSUS = "cursus";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_BEGIN_AT)
    public Date begin_at;
    @Nullable
    @SerializedName(API_END_AT)
    public Date end_at;
    @Nullable
    @SerializedName(API_GRADE)
    public String grade;
    @SerializedName(API_LEVEL)
    public float level;
    @SerializedName(API_SKILLS)
    public List<Skills> skills;
    @SerializedName(API_CURSUS_ID)
    public int cursusId;
    @SerializedName(API_USER)
    public UsersLTE user;
    @SerializedName(API_CURSUS)
    public Cursus cursus;

    public static class Skills {

        static final String API_ID = "id";
        static final String API_NAME = "name";
        static final String API_LEVEL = "level";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_NAME)
        public String name;
        @SerializedName(API_LEVEL)
        public float level;

    }
}
