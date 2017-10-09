package com.paulvarry.intra42.activities.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.ViewStatePagerAdapter;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsLTE;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.Teams;
import com.paulvarry.intra42.api.model.Users;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ProjectActivity extends BasicTabActivity
        implements ProjectOverviewFragment.OnFragmentInteractionListener, ProjectUserFragment.OnFragmentInteractionListener,
        ProjectAttachmentsFragment.OnFragmentInteractionListener, ProjectSubFragment.OnFragmentInteractionListener,
        ProjectUsersListFragment.OnFragmentInteractionListener, BasicThreadActivity.GetDataOnThread {

    private static final String INTENT_ID_PROJECT_USER = "project_user_id";
    private static final String INTENT_ID_USER = "P_user_id";
    private static final String INTENT_SLUG_USER = "p_user_login";
    private static final String INTENT_ID_PROJECT = "p_project_id";
    private static final String INTENT_SLUG_PROJECT = "p_project_slug";
    private static final String INTENT_JSON_USER = "user_json";
    private static final String INTENT_JSON_PROJECT = "project_json";

    public AppClass app;
    ProjectUser projectUser;
    private int idProjectUser;
    private int idUser;
    private String login;
    private int idProject;
    private String slugProject;

    public static void openIt(Context context, ProjectsUsers userCursusProjects) {
        Intent intent = new Intent(context, ProjectActivity.class);
        if (userCursusProjects != null) {
            intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, userCursusProjects.project.slug);
            intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, userCursusProjects.project.id);
            intent.putExtra(ProjectActivity.INTENT_ID_PROJECT_USER, userCursusProjects.id);
            if (userCursusProjects.user != null) {
                intent.putExtra(ProjectActivity.INTENT_ID_USER, userCursusProjects.user.id);
            }
        }
        context.startActivity(intent);
    }

    public static void openIt(Context context, ProjectsLTE project) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, project.id);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, project.slug);
        context.startActivity(intent);
    }

    public static void openIt(Context context, Projects project) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, project.id);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, project.slug);
        context.startActivity(intent);
    }

    public static void openIt(Context context, ProjectsLTE project, int idUser) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, project.id);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, project.slug);
        intent.putExtra(ProjectActivity.INTENT_ID_USER, idUser);
        context.startActivity(intent);
    }

    public static void openIt(Context context, Projects project, int userId) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, project.id);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, project.slug);
        intent.putExtra(ProjectActivity.INTENT_ID_USER, userId);
        context.startActivity(intent);
    }

    public static void openIt(Context context, String projectSlug, int idUser) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, projectSlug);
        intent.putExtra(ProjectActivity.INTENT_ID_USER, idUser);
        context.startActivity(intent);
    }

    public static void openIt(Context context, String projectSlug) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, projectSlug);
        context.startActivity(intent);
    }

    @Deprecated
    public static void openIt(Context context, String projectSlug, String login) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, projectSlug);
        intent.putExtra(ProjectActivity.INTENT_SLUG_USER, login);
        context.startActivity(intent);
    }

    public static Intent getIntent(Context context, int idProject) {
        if (idProject != 0) {
            Intent intent = new Intent(context, UserActivity.class);
            intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, idProject);
            return intent;
        }
        return null;
    }

    @Deprecated
    public static Intent getIntent(Context context, int idProject, UsersLTE user) {
        if (idProject != 0 && user != null) {
            Intent intent = new Intent(context, ProjectActivity.class);
            intent.putExtra(ProjectActivity.INTENT_ID_PROJECT, idProject);
            intent.putExtra(ProjectActivity.INTENT_ID_USER, user.id);
            intent.putExtra(ProjectActivity.INTENT_SLUG_USER, user.login);
            return intent;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //handle just logged users.
        Intent intent = getIntent();
        String action = intent.getAction();
        app = (AppClass) getApplication();

        if (Intent.ACTION_VIEW.equals(action)) {
            if (intent.getData().getHost().equals("projects.intra.42.fr")) {
                List<String> pathSegments = intent.getData().getPathSegments();
                if (pathSegments.size() == 2) {
                    if (pathSegments.get(0).equals("projects"))
                        slugProject = pathSegments.get(1);
                    else {
                        slugProject = pathSegments.get(0);
                        if (pathSegments.get(1).equals("mine"))
                            login = app.me.login;
                        else
                            login = pathSegments.get(1);
                    }
                }
            }
        }

        if (intent.hasExtra(INTENT_ID_PROJECT_USER))
            idProjectUser = intent.getIntExtra(INTENT_ID_PROJECT_USER, 0);
        if (intent.hasExtra(INTENT_ID_PROJECT))
            idProject = intent.getIntExtra(INTENT_ID_PROJECT, 0);
        if (intent.hasExtra(INTENT_SLUG_PROJECT))
            slugProject = intent.getStringExtra(INTENT_SLUG_PROJECT);
        if (intent.hasExtra(INTENT_ID_USER))
            idUser = intent.getIntExtra(INTENT_ID_USER, 0);
        if (intent.hasExtra(INTENT_SLUG_USER))
            login = intent.getStringExtra(INTENT_SLUG_USER);

        registerGetDataOnOtherThread(this);

        super.onCreate(savedInstanceState);
        super.setSelectedMenu(Navigation.MENU_SELECTED_PROJECTS);
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        return null;
    }

    @Override
    public void setupViewPager(ViewPager viewPager) {
        ViewStatePagerAdapter adapter = new ViewStatePagerAdapter(getSupportFragmentManager());
        if (projectUser != null && projectUser.project != null) {
            adapter.addFragment(ProjectOverviewFragment.newInstance(), getString(R.string.title_tab_project_overview));
            if (projectUser.user != null && projectUser.user.user != null) {
                String user;
                if (projectUser.user.user.equals(app.me))
                    user = getString(R.string.title_tab_project_me);
                else
                    user = projectUser.user.user.login;
                adapter.addFragment(ProjectUserFragment.newInstance(), user);
            }
            if (projectUser.project.children != null && !projectUser.project.children.isEmpty())
                adapter.addFragment(ProjectSubFragment.newInstance(), getString(R.string.project_sub_projects));
            if (projectUser.project != null && projectUser.project.attachments != null && !projectUser.project.attachments.isEmpty())
                adapter.addFragment(ProjectAttachmentsFragment.newInstance(), getString(R.string.title_tab_project_attachments));
            adapter.addFragment(ProjectUsersListFragment.newInstance(), getString(R.string.title_tab_project_users));
        } else
            setViewState(StatusCode.EMPTY);
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void getDataOnOtherThread() throws IOException, UnauthorizedException, ErrorException {
        ApiService apiService = app.getApiService();

        if (idProjectUser != 0)
            if (slugProject != null && !slugProject.isEmpty())
                projectUser = ProjectUser.get(apiService, idProjectUser, slugProject);
            else
                projectUser = ProjectUser.get(apiService, idProjectUser, idProject);
        else {
            if (login != null) {
                if (slugProject != null)
                    projectUser = ProjectUser.getWithProject(apiService, slugProject, login);
                else
                    projectUser = ProjectUser.getWithProject(apiService, idProject, login);
            } else {
                if (idUser == 0)
                    idUser = app.me.id;
                if (slugProject != null)
                    projectUser = ProjectUser.getWithProject(apiService, slugProject, idUser);
                else
                    projectUser = ProjectUser.getWithProject(apiService, idProject, idUser);
            }
        }
    }

    @Override
    public String getToolbarName() {
        if (projectUser != null && projectUser.project != null) {
            if (!projectUser.project.isMaster()) {
                ActionBar actionBar = super.getSupportActionBar();
                if (actionBar != null)
                    actionBar.setSubtitle(projectUser.project.parent.name);
            }
            return projectUser.project.name;
        } else if (slugProject != null)
            return slugProject;
        return null;
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    public static class ProjectUser {

        public Projects project;
        public ProjectsUsers user;

        static ProjectUser getWithProject(ApiService api, int projectId, int userId) throws IOException, UnauthorizedException, ErrorException {
            Call<Projects> callProject = api.getProject(projectId);
            Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectId, userId);
            Call<List<Teams>> callTeams = api.getTeams(userId, projectId, 1);

            return getWithProject(callProject, callProjectUsers, callTeams);
        }

        static ProjectUser getWithProject(ApiService api, int projectId, String login) throws IOException, UnauthorizedException, ErrorException {
            Call<Projects> callProject = api.getProject(projectId);
            Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectId, login);
            Call<List<Teams>> callTeams = api.getTeams(login, projectId, 1);

            return getWithProject(callProject, callProjectUsers, callTeams);
        }

        static ProjectUser getWithProject(ApiService api, String projectSlug, String userLogin) throws IOException, UnauthorizedException, ErrorException {
            Response<Users> response = api.getUser(userLogin).execute();
            if (!Tools.apiIsSuccessful(response))
                return null;
            Call<Projects> callProject = api.getProject(projectSlug);
            Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectSlug, response.body().id);
            Call<List<Teams>> callTeams = api.getTeams(userLogin, projectSlug, 1);

            return getWithProject(callProject, callProjectUsers, callTeams);
        }

        static ProjectUser getWithProject(ApiService api, String projectSlug, int userId) throws IOException, UnauthorizedException, ErrorException {
            Call<Projects> callProject = api.getProject(projectSlug);
            Call<List<ProjectsUsers>> callProjectUsers = api.getProjectsUsers(projectSlug, userId);
            Call<List<Teams>> callTeams = api.getTeams(userId, projectSlug, 1);

            return getWithProject(callProject, callProjectUsers, callTeams);
        }

        static ProjectUser getWithProject(Call<Projects> callProject, Call<List<ProjectsUsers>> callProjectUsers, Call<List<Teams>> callTeams) throws IOException, UnauthorizedException, ErrorException {
            Response<Projects> repProject;
            Response<List<ProjectsUsers>> repProjectUsers;
            Response<List<Teams>> repTeams;

            repProject = callProject.execute();
            repProjectUsers = callProjectUsers.execute();
            repTeams = callTeams.execute();

            ProjectUser p = new ProjectUser();
            if (Tools.apiIsSuccessful(repProject))
                p.project = repProject.body();

            if (repProjectUsers != null && repProjectUsers.code() == 200 && repProjectUsers.body().size() != 0 &&
                    repTeams != null && repTeams.code() == 200) {
                p.user = repProjectUsers.body().get(0);
                p.user.teams = repTeams.body();
            }
            return p;
        }

        public static ProjectUser get(ApiService api, int projectUserId, int projectId) throws IOException, ErrorException, UnauthorizedException {
            Call<Projects> callProject = api.getProject(projectId);
            Call<ProjectsUsers> callProjectUsers = api.getProjectsUsers(projectUserId);


            Response<Projects> repProject;
            Response<ProjectsUsers> repProjectUsers;
            Response<List<Teams>> repTeams;

            repProject = callProject.execute();
            repProjectUsers = callProjectUsers.execute();


            ProjectUser p = new ProjectUser();
            if (Tools.apiIsSuccessful(repProject))
                p.project = repProject.body();

            if (Tools.apiIsSuccessful(repProjectUsers)) {
                p.user = repProjectUsers.body();

                if (p.user.user == null)
                    throw new ErrorException();

                Call<List<Teams>> callTeams = api.getTeams(p.user.user.id, projectId, 1);

                repTeams = callTeams.execute();

                if (Tools.apiIsSuccessful(repTeams))
                    p.user.teams = repTeams.body();
            }
            return p;
        }

        public static ProjectUser get(ApiService api, int projectUserId, String projectSlug) throws UnauthorizedException, ErrorException, IOException {
            Call<Projects> callProject = api.getProject(projectSlug);
            Call<ProjectsUsers> callProjectUsers = api.getProjectsUsers(projectUserId);

            Response<Projects> repProject;
            Response<ProjectsUsers> repProjectUsers;
            Response<List<Teams>> repTeams;

            ProjectUser p = new ProjectUser();
            repProject = callProject.execute();
            if (Tools.apiIsSuccessful(repProject))
                p.project = repProject.body();

            repProjectUsers = callProjectUsers.execute();
            if (Tools.apiIsSuccessful(repProjectUsers)) {
                p.user = repProjectUsers.body();

                if (p.user.user == null)
                    throw new ErrorException();

                Call<List<Teams>> callTeams = api.getTeams(p.user.user.id, projectSlug, 1);

                repTeams = callTeams.execute();

                if (Tools.apiIsSuccessful(repTeams))
                    p.user.teams = repTeams.body();
            }
            return p;
        }
    }

}
