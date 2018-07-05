package com.paulvarry.intra42.api.model;


import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.api.IBaseItemMedium;
import com.paulvarry.intra42.utils.AppSettings;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Projects extends ProjectsLTE implements IBaseItemMedium {

    private static final String API_ID = "id";
    private static final String API_NAME = "name";
    private static final String API_SLUG = "slug";
    private static final String API_DESCRIPTION = "description";
    private static final String API_RECOMMENDATION = "recommendation";
    private static final String API_PARENT = "parent";
    private static final String API_CHILDREN = "children";
    private static final String API_OBJECTIVES = "objectives";
    private static final String API_URL = "url";
    private static final String API_PROJECT_USERS_URL = "project_users_url";
    private static final String API_TEAMS_URL = "teams_url";
    private static final String API_TIER = "tier";
    private static final String API_CURSUS = "cursus";
    private static final String API_CAMPUS = "campus";
    private static final String API_ATTACHMENTS = "attachments";
    private static final String API_SKILLS = "skills";
    private static final String API_TAGS = "tags";
    private static final String API_PROJECT_SESSIONS = "project_sessions";

    @SerializedName(API_DESCRIPTION)
    public String description;
    @SerializedName(API_RECOMMENDATION)
    public String recommendation;
    @SerializedName(API_PARENT)
    public ProjectsLTE parent;
    @SerializedName(API_CHILDREN)
    public List<ProjectsLTE> children;
    @SerializedName(API_OBJECTIVES)
    public List<String> objectives;
    @SerializedName(API_URL)
    public String url;
    @SerializedName(API_PROJECT_USERS_URL)
    public String projectUsersUrl;
    @SerializedName(API_TEAMS_URL)
    public String teamsUrl;
    @SerializedName(API_TIER)
    public Integer tier;
    @SerializedName(API_CURSUS)
    public List<Cursus> cursusList;
    @SerializedName(API_CAMPUS)
    public List<Campus> campusList;
    @SerializedName(API_ATTACHMENTS)
    public List<Attachments> attachments;
    @SerializedName(API_SKILLS)
    public List<Skills> skills;
    @SerializedName(API_TAGS)
    public List<Tags> tags;
    @SerializedName(API_PROJECT_SESSIONS)
    public List<ProjectsSessions> sessionsList;

    public boolean isMaster() {
        return parent == null;
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public String getSub(Context context) {
        return slug;
    }

    @Override
    public boolean openIt(Context context) {
        ProjectActivity.openIt(context, this);
        return true;
    }

    @Override
    public String getDetail(Context context) {
        ProjectsSessions sessions = ProjectsSessions.getSessionSubscribable(sessionsList);
        String time = null;

        if (sessions != null) {
            PrettyTime p = new PrettyTime(Locale.getDefault());
            time = p.formatDuration(new Date(System.currentTimeMillis() - sessions.estimateTime * 1000));
        }

        String ret = "";
        if (time != null)
            ret += time + " ";
        ret += "T" + String.valueOf(tier);

        return ret;
    }

    public boolean shouldDisplayOnApp(AppClass app) {
        int campus = AppSettings.getAppCampus(app);
        int cursus = AppSettings.getAppCursus(app);
        return shouldDisplayOnApp(cursus, campus);
    }

    public boolean shouldDisplayOnApp(int cursus, int campus) {
        boolean cursusOk = false;
        boolean campusOk = false;

        if (cursus <= 0 && campus <= 0)
            return true;

        if (cursus > 0) {
            for (Cursus c : cursusList) {
                if (c.id == cursus) {
                    cursusOk = true;
                    break;
                }
            }
        }
        if (campus > 0) {
            for (Campus c : campusList) {
                if (c.id == campus) {
                    campusOk = true;
                    break;
                }
            }
        }
        return campusOk && cursusOk;
    }
}
