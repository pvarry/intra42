package com.paulvarry.intra42.api.pack;

import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.Teams;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProjectUser {

    public Projects project;
    public ProjectsUsers user;

    public static ProjectUser getWithProject(ApiService api, int projectId, int userId) {
        Call<Projects> callProject = api.getProject(projectId);
        Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectId, userId);
        Call<List<Teams>> callTeams = api.getTeams(userId, projectId, 1);

        return getWithProject(callProject, callProjectUsers, callTeams);
    }

    public static ProjectUser getWithProject(ApiService api, int projectId, String login) {
        Call<Projects> callProject = api.getProject(projectId);
        Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectId, login);
        Call<List<Teams>> callTeams = api.getTeams(login, projectId, 1);

        return getWithProject(callProject, callProjectUsers, callTeams);
    }

    @Deprecated
    public static ProjectUser getWithProject(ApiService api, String projectSlug, String userLogin) {
        Call<Projects> callProject = api.getProject(projectSlug);
        Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectSlug, userLogin);
        Call<List<Teams>> callTeams = api.getTeams(userLogin, projectSlug, 1);

        return getWithProject(callProject, callProjectUsers, callTeams);
    }

    public static ProjectUser getWithProject(ApiService api, String projectSlug, int userId) {
        Call<Projects> callProject = api.getProject(projectSlug);
        Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectSlug, userId);
        Call<List<Teams>> callTeams = api.getTeams(userId, projectSlug, 1);

        return getWithProject(callProject, callProjectUsers, callTeams);
    }

    public static ProjectUser getWithProject(Call<Projects> callProject, Call<List<ProjectsUsers>> callProjectUsers, Call<List<Teams>> callTeams) {
        Response<Projects> repProject = null;
        Response<List<ProjectsUsers>> repProjectUsers = null;
        Response<List<Teams>> repTeams = null;
        try {
            repProject = callProject.execute();
            repProjectUsers = callProjectUsers.execute();
            repTeams = callTeams.execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ProjectUser p = new ProjectUser();
        if (repProject != null && repProject.code() == 200)
            p.project = repProject.body();

        if (repProjectUsers != null && repProjectUsers.code() == 200 && repProjectUsers.body().size() != 0 &&
                repTeams != null && repTeams.code() == 200) {
            p.user = repProjectUsers.body().get(0);
            p.user.teams = repTeams.body();
        }
        return p;
    }

    public static ProjectUser get(ApiService api, int projectUserId, int projectId) {
        Call<Projects> callProject = api.getProject(projectId);
        Call<ProjectsUsers> callProjectUsers = api.getProjectsUsers(projectUserId);


        Response<Projects> repProject = null;
        Response<ProjectsUsers> repProjectUsers = null;
        Response<List<Teams>> repTeams = null;
        try {
            repProject = callProject.execute();
            repProjectUsers = callProjectUsers.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ProjectUser p = new ProjectUser();
        if (repProject != null && repProject.code() == 200)
            p.project = repProject.body();

        if (repProjectUsers != null && repProjectUsers.code() == 200) {
            p.user = repProjectUsers.body();

            Call<List<Teams>> callTeams = api.getTeams(p.user.user.id, projectId, 1);
            try {
                repTeams = callTeams.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (repTeams != null && repTeams.code() == 200)
                p.user.teams = repTeams.body();
        }
        return p;
    }

    public static ProjectUser get(ApiService api, int projectUserId, String projectSlug) {
        Call<Projects> callProject = api.getProject(projectSlug);
        Call<ProjectsUsers> callProjectUsers = api.getProjectsUsers(projectUserId);

        Response<Projects> repProject = null;
        Response<ProjectsUsers> repProjectUsers = null;
        Response<List<Teams>> repTeams = null;
        try {
            repProject = callProject.execute();
            repProjectUsers = callProjectUsers.execute();

        } catch (IOException e) {
            e.printStackTrace();
        }

        ProjectUser p = new ProjectUser();
        if (repProject != null && repProject.code() == 200)
            p.project = repProject.body();

        if (repProjectUsers != null && repProjectUsers.code() == 200) {
            p.user = repProjectUsers.body();

            Call<List<Teams>> callTeams = api.getTeams(p.user.user.id, projectSlug, 1);
            try {
                repTeams = callTeams.execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (repTeams != null && repTeams.code() == 200)
                p.user.teams = repTeams.body();
        }
        return p;
    }
}
