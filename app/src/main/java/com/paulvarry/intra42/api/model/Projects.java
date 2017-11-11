package com.paulvarry.intra42.api.model;


import android.content.Context;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.api.BaseItem;

import java.util.List;

public class Projects extends ProjectsLTE implements BaseItem {

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
}
