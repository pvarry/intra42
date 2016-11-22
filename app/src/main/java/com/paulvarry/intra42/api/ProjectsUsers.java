package com.paulvarry.intra42.api;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.Tools.ProjectUserStatus;

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
    public String status;
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
    public UserLTE user;
    @Nullable
    @SerializedName(API_TEAMS)
    public transient List<Teams> teams;

    static public List<ProjectsUsers> getListDoing(List<ProjectsUsers> projects) {
        return getListCursus(projects, null);
    }

    static public List<ProjectsUsers> getListCursusDoing(List<ProjectsUsers> projects, CursusUsers userCursus) {
        List<ProjectsUsers> ret = new ArrayList<>();

        if (projects != null && !projects.isEmpty()) {
            for (ProjectsUsers p : projects) {
                if (!p.status.equals(ProjectUserStatus.FINISHED) &&
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

    static public List<ProjectsUsers> getListOnlyRoot(List<ProjectsUsers> list) { // c pas propre mais il y pas d'aiutres moyens
        List<ProjectsUsers> ret = new ArrayList<>();

        for (ProjectsUsers p : list) {
            String slug = p.project.slug;
            if (!slug.contains("piscine-cpp-") &&
                    !slug.contains("first-internship-") &&
                    !slug.contains("piscine-php-") &&
                    !slug.contains("piscine-after-effect-") &&
                    !slug.contains("electronics-") &&
                    !slug.contains("piscine-c-") &&
                    !slug.contains("piscine-ocaml-") &&
                    !slug.contains("piscine-unity-"))
                ret.add(p);
        }
        return ret;
    }

}
