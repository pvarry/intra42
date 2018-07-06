package com.paulvarry.intra42.api.model;

import android.content.Context;
import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.IBaseItemSmall;
import com.paulvarry.intra42.api.ItemWithId;

import java.util.Date;
import java.util.List;

/**
 * A user (or a groupe of users) register in a project
 */
public class Teams extends ItemWithId implements IBaseItemSmall {

    private static final String API_NAME = "name";
    private static final String API_URL = "url";
    private static final String API_FINAL_MARK = "final_mark";
    private static final String API_PROJECT_ID = "project_id";
    private static final String API_CREATED_AT = "created_at";
    private static final String API_UPDATED_AT = "updated_at";
    private static final String API_STATUS = "status";
    private static final String API_TERMINATING_AT = "terminating_at";
    private static final String API_USERS = "users";
    private static final String API_LOCKED = "locked?";
    private static final String API_VALIDATED = "validated?";
    private static final String API_CLOSED = "closed?";
    private static final String API_REPO_URL = "repo_url";
    private static final String API_REPO_UUID = "repo_uuid";
    private static final String API_LOCKED_AT = "locked_at";
    private static final String API_CLOSED_AT = "closed_at";
    private static final String API_SCALE_TEAMS = "scale_teams";
    private static final String API_TEAMS_UPLOADS = "teams_uploads";

    @SerializedName(API_NAME)
    public String name;
    @SerializedName(API_URL)
    public String url;
    @Nullable
    @SerializedName(API_FINAL_MARK)
    public Integer finalMark;
    @SerializedName(API_PROJECT_ID)
    public int projectId;
    @SerializedName(API_CREATED_AT)
    public Date created_at;
    @SerializedName(API_UPDATED_AT)
    public Date updated_at;
    @SerializedName(API_STATUS)
    public ProjectsUsers.Status status;
    @SerializedName(API_TERMINATING_AT)
    public Date terminatingAt;
    @SerializedName(API_USERS)
    public List<TeamsUsers> users;
    @SerializedName(API_LOCKED)
    public boolean locked;
    @Nullable
    @SerializedName(API_VALIDATED)
    public Boolean validated;
    @SerializedName(API_CLOSED)
    public boolean closed;
    @SerializedName(API_REPO_URL)
    public String repoUrl;
    @SerializedName(API_REPO_UUID)
    public String repoUuid;
    @SerializedName(API_LOCKED_AT)
    public Date lockedAt;
    @SerializedName(API_CLOSED_AT)
    public Date closedAt;
    @Nullable
    @SerializedName(API_TEAMS_UPLOADS)
    public List<TeamsUploads> teamsUploads;
    @Nullable
    @SerializedName(API_SCALE_TEAMS)
    public List<ScaleTeams> scaleTeams;

    public transient boolean extraAdded = false;

    public TeamsUsers getLeader() {
        if (users == null || users.isEmpty())
            return null;
        for (TeamsUsers user : users) {
            if (user.leader)
                return user;
        }
        return users.get(0);
    }

    @Override
    public String getName(Context context) {
        return name;
    }

    @Override
    public String getSub(Context context) {
        return null;
    }

    @Override
    public boolean openIt(Context context) {
        return false;
    }

    @Override
    public int getId() {
        return id;
    }
}