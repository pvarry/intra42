package com.paulvarry.intra42.activity.project;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.paulvarry.intra42.Adapter.ViewPagerAdapter;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activity.user.UserActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsLTE;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.api.pack.ProjectUser;
import com.paulvarry.intra42.ui.BasicActivity;
import com.paulvarry.intra42.ui.BasicTabActivity;
import com.paulvarry.intra42.ui.tools.Navigation;

import java.util.List;

public class ProjectActivity extends BasicTabActivity
        implements ProjectOverviewFragment.OnFragmentInteractionListener, ProjectUserFragment.OnFragmentInteractionListener,
        ProjectAttachmentsFragment.OnFragmentInteractionListener, ProjectSubFragment.OnFragmentInteractionListener,
        ProjectUsersListFragment.OnFragmentInteractionListener {

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

    public static void openIt(Context context, String projectSlug) {
        Intent intent = new Intent(context, ProjectActivity.class);
        intent.putExtra(ProjectActivity.INTENT_SLUG_PROJECT, projectSlug);
        context.startActivity(intent);
    }

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
        String type = intent.getType();
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
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        if (projectUser != null && projectUser.project != null) {
            adapter.addFragment(ProjectOverviewFragment.newInstance(), getString(R.string.tab_project_overview));
            if (projectUser.user != null && projectUser.user.user != null) {
                String user;
                if (projectUser.user.user.equals(app.me))
                    user = getString(R.string.tab_project_me);
                else
                    user = projectUser.user.user.login;
                adapter.addFragment(ProjectUserFragment.newInstance(), user);
            }
            if (projectUser.project.children != null && !projectUser.project.children.isEmpty())
                adapter.addFragment(ProjectSubFragment.newInstance(), getString(R.string.sub_projects));
            if (projectUser.project != null && projectUser.project.attachments != null && !projectUser.project.attachments.isEmpty())
                adapter.addFragment(ProjectAttachmentsFragment.newInstance(), getString(R.string.tab_project_attachments));
            adapter.addFragment(ProjectUsersListFragment.newInstance(), getString(R.string.tab_project_users));
        } else
            Toast.makeText(ProjectActivity.this, "You not allowed", Toast.LENGTH_SHORT).show();
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean getDataOnOtherThread() {
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

//        if (!projectUser.project.tags.isEmpty()) {
//            List<Notions> notions = Notions.get(this, api, projectUser.project.tags.get(0).id);
//
//            if (projectUser.project.attachments == null)
//                projectUser.project.attachments = new ArrayList<>();
//
//            if (notions != null && !notions.isEmpty()) {
//                List<Subnotions> subnotions = Subnotions.get(this, api, notions.get(0).id);
//                if (subnotions != null) {
//                    for (Subnotions title : subnotions)
//                        projectUser.project.attachments.addAll(title.attachments);
//                }
//            }
//
//        }
        return true;
    }

    @Override
    public boolean getDataOnMainThread() {
        return false;
    }

    @Override
    public String getToolbarName() {
        if (projectUser != null && projectUser.project != null)
            if (projectUser.project.isMaster())
                return projectUser.project.name;
            else
                return projectUser.project.parent.name + " / " + projectUser.project.name;
        else if (slugProject != null)
            return slugProject;
        return null;
    }

    /**
     * This text is useful when both {@link BasicActivity#getDataOnMainThread()} and {@link BasicActivity#getDataOnOtherThread()} return false.
     *
     * @return A simple text to display on screen, may return null;
     */
    @Override
    public String getEmptyText() {
        return null;
    }
}
