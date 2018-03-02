package com.paulvarry.intra42.api.model;

import android.support.annotation.Nullable;
import android.support.annotation.StringRes;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.R;

import java.util.ArrayList;
import java.util.List;

public class ProjectsUsers {

    private final static String API_ID = "id";
    private final static String API_OCCURRENCE = "occurrence";
    private final static String API_FINAL_MARK = "final_mark";
    private final static String API_STATUS = "status";
    private final static String API_VALIDATED = "validated?";
    private final static String API_CURRENT_TEAM_ID = "current_team_id";
    private final static String API_PROJECT = "project";
    private final static String API_CURSUS_IDS = "cursus_ids";
    private final static String API_USER = "user";
    private final static String API_TEAMS = "teams";

    @SerializedName(API_ID)
    public int id;
    @SerializedName(API_OCCURRENCE)
    public int occurrence;
    @Nullable
    @SerializedName(API_FINAL_MARK)
    public Integer finalMark;
    @SerializedName(API_STATUS)
    public Status status;
    @Nullable
    @SerializedName(API_VALIDATED)
    public Boolean validated;
    @Nullable
    @SerializedName(API_CURRENT_TEAM_ID)
    public Integer currentTeamId;
    @SerializedName(API_PROJECT)
    public ProjectsLTE project;
    @SerializedName(API_CURSUS_IDS)
    public List<Integer> cursusIds;
    @Nullable
    @SerializedName(API_USER)
    public UsersLTE user;
    @Nullable
    @SerializedName(API_TEAMS)
    public List<Teams> teams;

    static public List<ProjectsUsers> getListDoing(List<ProjectsUsers> projects) {
        List<ProjectsUsers> ret = new ArrayList<>();

        for (ProjectsUsers p : projects) {
            if (p.status != Status.FINISHED)
                ret.add(p);
        }
        return ret;
    }

    static public List<ProjectsUsers> getListCursusDoing(List<ProjectsUsers> projects, CursusUsers userCursus) {
        List<ProjectsUsers> ret = new ArrayList<>();

        if (projects != null && !projects.isEmpty()) {
            for (ProjectsUsers p : projects) {
                if (p.status != Status.FINISHED &&
                        ((userCursus != null && p.cursusIds != null && p.cursusIds.contains(userCursus.cursusId)) || userCursus == null || p.cursusIds == null))
                    ret.add(p);
            }
        }

        return ret;
    }

    static public List<ProjectsUsers> getListCursus(List<ProjectsUsers> list, Integer cursus) {
        List<ProjectsUsers> ret = new ArrayList<>();

        for (ProjectsUsers p : list) {
            if ((p.cursusIds != null && p.cursusIds.contains(cursus)) || p.cursusIds == null || cursus == null)
                ret.add(p);
        }
        return ret;
    }

    static public List<ProjectsUsers> getListOnlyRoot(List<ProjectsUsers> list) {
        List<ProjectsUsers> ret = new ArrayList<>();

        for (ProjectsUsers p : list) {
            if (p.project.parentId == null)
                ret.add(p);
        }
        return ret;
    }

    public enum Status {

        @SerializedName("searching_a_group")SEARCHING_A_GROUP(R.string.project_user_status_searching_a_group),
        @SerializedName("finished")FINISHED(R.string.project_user_status_finished),
        @SerializedName("in_progress")IN_PROGRESS(R.string.project_user_status_in_progress),
        @SerializedName("waiting_for_correction")WAITING_FOR_CORRECTION(R.string.project_user_status_waiting_for_correction),
        @SerializedName("waiting_to_start")WAITING_TO_START(R.string.project_user_status_waiting_to_start),
        @SerializedName("creating_group")CREATING_GROUP(R.string.project_user_status_creating_group),
        @SerializedName("parent")PARENT(R.string.project_user_status_parent),
        @SerializedName("unknown")unknown(R.string.project_user_status_unknown);

        private final int res;

        Status(@StringRes int res) {
            this.res = res;
        }

        public int getRes() {
            return res;
        }

    }

    public class ProjectsLTE extends com.paulvarry.intra42.api.model.ProjectsLTE {

        private static final String API_PARENT_ID = "parent_id";

        @SerializedName(API_PARENT_ID)
        public Integer parentId;

    }


}
