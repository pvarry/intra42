package com.paulvarry.intra42.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.Tools.AppSettings;

import java.util.Date;
import java.util.List;

public class ProjectsSessions {

    private static final String API_ID = "id";
    private static final String API_SOLO = "solo";
    private static final String API_BEGIN_AT = "begin_at";
    private static final String API_END_AT = "end_at";
    private static final String API_ESTIMATE_TIME = "estimate_time";
    private static final String API_DURATION_DAYS = "duration_days";
    private static final String API_TERMINATING_AFTER = "terminating_after";
    private static final String API_PROJECT_ID = "project_id";
    private static final String API_CAMPUS_ID = "campus_id";
    private static final String API_CURSUS_ID = "cursus_id";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPDATED_AT = "updated_at";
    private static final String API_MAX_PEOPLE = "max_people";
    private static final String API_IS_SUBSCRIPTABLE = "is_subscriptable";
    private static final String API_SCALES = "scales";
    private static final String API_UPLOADS = "uploads";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_SOLO)
    public boolean solo;
    @SerializedName(API_BEGIN_AT)
    public Date beginAt;
    @SerializedName(API_END_AT)
    public Date endAt;
    @SerializedName(API_ESTIMATE_TIME)
    public long estimateTime;
    @SerializedName(API_PROJECT_ID)
    public int projectId;
    @Nullable
    @SerializedName(API_CAMPUS_ID)
    public Integer campusId;
    @Nullable
    @SerializedName(API_CURSUS_ID)
    public Integer cursusId;
    @SerializedName(API_CREATED_AT)
    public Date createdAt;
    @SerializedName(API_UPDATED_AT)
    public Date updatedAt;
    @Nullable
    @SerializedName(API_MAX_PEOPLE)
    public Integer maxPeople;
    @SerializedName(API_IS_SUBSCRIPTABLE)
    public boolean isSubscribable;
    @SerializedName(API_SCALES)
    public List<Scales> scales;
    public List<Object> uploads;

    static public ProjectsSessions getSessionSubscribable(List<ProjectsSessions> sessionses) {
        for (ProjectsSessions item : sessionses) {
            if (item.isSubscribable)
                return item;
        }
        return null;
    }

    public static ProjectsSessions getScaleForMe(Context context, List<ProjectsSessions> sessions) {
        if (sessions == null)
            return null;
        SharedPreferences preferences = AppSettings.getSharedPreferences(context);
        int campus = AppSettings.ContentOption.getCampus(preferences);
        int cursus = AppSettings.ContentOption.getCursus(preferences);

        for (ProjectsSessions s : sessions) {
            if (campus != 0 && campus != -1 && s.campusId != null && s.campusId != campus)
                continue;
            if (cursus != 0 && cursus != -1 && s.cursusId != null && s.cursusId != cursus)
                continue;
            if (!s.isSubscribable)
                continue;
            return s;
        }
        return null;
    }

    public static class Scales {
        private static final String API_ID = "id";
        private static final String API_CORRECTION_NUMBER = "correction_number";
        private static final String API_IS_PRIMARY = "is_primary";

        @SerializedName(API_ID)
        public int id;
        @SerializedName(API_CORRECTION_NUMBER)
        public int correctionNumber;
        @SerializedName(API_IS_PRIMARY)
        public boolean isPrimary;

        static public Scales getPrimary(List<Scales> scales) {
            if (scales != null)
                for (Scales s : scales) {
                    if (s.isPrimary)
                        return s;
                }
            return null;
        }
    }
}
