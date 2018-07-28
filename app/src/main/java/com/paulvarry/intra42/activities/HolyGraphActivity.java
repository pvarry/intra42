package com.paulvarry.intra42.activities;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.adapters.ListAdapterMarks;
import com.paulvarry.intra42.api.ApiServiceAuthServer;
import com.paulvarry.intra42.api.model.ProjectDataIntra;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.bottomSheet.BottomSheetProjectsGalaxyFragment;
import com.paulvarry.intra42.ui.BasicThreadActivity;
import com.paulvarry.intra42.ui.Galaxy;
import com.paulvarry.intra42.ui.tools.Navigation;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.GalaxyUtils;
import com.paulvarry.intra42.utils.Tools;

import java.io.IOException;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Response;

public class HolyGraphActivity extends BasicThreadActivity implements AdapterView.OnItemSelectedListener, BasicThreadActivity.GetDataOnThread, Galaxy.OnProjectClickListener, AdapterView.OnItemClickListener, BasicThreadActivity.GetDataOnMain {

    private static final String STATE_POSITION = "state_spinner_position";

    int cursus;
    int campus;
    private Spinner spinner;
    private ListView listView;
    private ListView listViewAll;
    private TextView textViewNoItem;
    private Galaxy galaxy;
    private int spinnerSelected = -1;
    private List<ProjectDataIntra> galaxyData;
    private List<ProjectsUsers> displayList = null;

    public static void openIt(Context context) {
        Intent intent = new Intent(context, HolyGraphActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_holy_graph);

        cursus = AppSettings.getAppCursus(app);
        campus = AppSettings.getAppCampus(app);

        Context context = this;
        ActionBar bar = getSupportActionBar();
        if (bar != null)
            context = bar.getThemedContext();
        SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(context,
                R.array.spinner_projects_galaxy, android.R.layout.simple_spinner_dropdown_item);

        spinner = new AppCompatSpinner(this);
        spinner.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        spinner.setAdapter(spinnerAdapter);
        spinner.setOnItemSelectedListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Toolbar.LayoutParams lp = new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMarginStart((int) Tools.dpToPx(context, 8));
            spinner.setLayoutParams(lp);
        }

        if (savedInstanceState != null)
            spinner.setSelection(savedInstanceState.getInt(STATE_POSITION));

        toolbar.addView(spinner);
        toolbar.setContentInsetStartWithNavigation((int) Tools.dpToPx(context, 8));

        listView = findViewById(R.id.listView);
        listViewAll = findViewById(R.id.listViewAll);
        textViewNoItem = findViewById(R.id.textViewNoItem);
        galaxy = findViewById(R.id.galaxy);

        galaxy.setOnProjectClickListener(this);
        listView.setOnItemClickListener(this);
        listViewAll.setOnItemClickListener(this);

        super.registerGetDataOnOtherThread(this);
        super.registerGetDataOnMainTread(this);
        super.setActionBarToggle(ActionBarToggle.HAMBURGER);
        super.setSelectedMenu(Navigation.MENU_SELECTED_PROJECTS);

        super.onCreateFinished();
    }

    @Nullable
    @Override
    public String getUrlIntra() {
        switch (spinnerSelected) {
            case 0:
                return "https://projects.intra.42.fr/projects/graph?login=" + app.me.login;
            case 1:
                return "https://projects.intra.42.fr/projects/list";
        }
        return "https://projects.intra.42.fr/";
    }

    @Override
    public String getToolbarName() {
        return "";
    }

    @Override
    protected void setViewContent() {
        if (spinnerSelected == 0) {
            galaxy.setVisibility(View.VISIBLE);
            setViewContentGalaxy();
        }
    }

    protected void setViewContentGalaxy() {

        if (galaxyData == null || galaxyData.isEmpty())
            textViewNoItem.setVisibility(View.VISIBLE);
        else {
            galaxy.bringToFront();
            galaxy.setData(galaxyData);
        }
    }

    @Override
    public String getEmptyText() {
        return null;
    }

    @Override
    public final Object onRetainCustomNonConfigurationInstance() {
        return galaxyData;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_POSITION, spinnerSelected);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void animate(View action, View view) {

        view.bringToFront();
        view.setVisibility(View.VISIBLE);

        if (action == null)
            return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

            if (!action.isAttachedToWindow() || !view.isAttachedToWindow())
                return;

            // finding X and Y co-ordinates
            int[] coordinateAction = {0, 0};
            int[] coordinateView = {0, 0};
            action.getLocationInWindow(coordinateAction);
            view.getLocationInWindow(coordinateView);
            int cx = (coordinateAction[0] + action.getWidth() / 2);
            int cy = (0 - coordinateView[1] + coordinateAction[1] + action.getHeight() / 2);

            // to find  radius when icon is tapped for showing layout
            int startRadius = 0;
            int endRadius = Math.max(view.getWidth() + cx, view.getHeight() + cy);

            // performing circular reveal when icon will be tapped
            Animator animator = ViewAnimationUtils.createCircularReveal(view, cx, cy, startRadius, endRadius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(350);

            // to show the layout when icon is tapped
            animator.start();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        if (spinnerSelected == -1 && position != 0) //force content if user select other data when Galaxy loading
            setViewState(StatusCode.CONTENT);

        if (spinnerSelected == position)
            return;
        spinnerSelected = position;
        if (position == 0) { // set Galaxy
            setViewContentGalaxy();
            animate(spinner, galaxy);
        } else {

            ListAdapterMarks adapterList;
            if (position == 1 && app.me.projectsUsers != null) { // set my projects
                displayList = app.me.projectsUsers;
                displayList = ProjectsUsers.getListOnlyRoot(displayList);
                displayList = ProjectsUsers.getListCursus(displayList, AppSettings.getAppCursus(app));
                if (displayList.isEmpty())
                    animate(spinner, textViewNoItem);
                else {
                    adapterList = new ListAdapterMarks(this, displayList);
                    listViewAll.setAdapter(adapterList);
                    animate(spinner, listViewAll);
                }
            } else if (position == 2) { // set in progress
                displayList = ProjectsUsers.getListCursusDoing(app.me.projectsUsers, AppSettings.getAppCursus(app));
                if (displayList.isEmpty())
                    animate(spinner, textViewNoItem);
                else {
                    adapterList = new ListAdapterMarks(this, displayList);
                    listView.setAdapter(adapterList);
                    animate(spinner, listView);
                }
            }

            if (displayList == null || displayList.size() != 0)
                textViewNoItem.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void getDataOnOtherThread() throws IOException, RuntimeException {
        ApiServiceAuthServer client = app.getApiServiceAuthServer();
        Call<List<ProjectDataIntra>> call = client.getGalaxy(cursus, campus, app.me.login);
        Response<List<ProjectDataIntra>> res = call.execute();
        if (Tools.apiIsSuccessfulNoThrow(res))
            galaxyData = res.body();
        else
            galaxyData = GalaxyUtils.getDataFromApp(this, cursus, campus, app.me);
        if (spinnerSelected == -1)
            spinnerSelected = 0;
    }

    @Override
    public ThreadStatusCode getDataOnMainThread() {
        galaxyData = (List<ProjectDataIntra>) getLastCustomNonConfigurationInstance();
        if (galaxyData != null && !galaxyData.isEmpty())
            return ThreadStatusCode.FINISH;
        return null;
    }

    @Override
    public void onClick(ProjectDataIntra projectData) {
        BottomSheetProjectsGalaxyFragment.openIt(this, projectData);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (displayList != null && displayList.size() > position)
            ProjectActivity.openIt(this, displayList.get(position).project);
    }
}
