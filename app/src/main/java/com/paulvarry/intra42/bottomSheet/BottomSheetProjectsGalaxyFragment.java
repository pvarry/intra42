package com.paulvarry.intra42.bottomSheet;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.ProjectDataIntra;

public class BottomSheetProjectsGalaxyFragment extends BottomSheetDialogFragment implements View.OnClickListener {

    private static final String ARG_PROJECT = "project_galaxy";

    ImageButton buttonOpen;

    AppClass appClass;
    ApiService api;

    ProjectDataIntra projectData;

    public static void openIt(FragmentActivity activity, ProjectDataIntra item) {
        BottomSheetProjectsGalaxyFragment bottomSheetDialogFragment = BottomSheetProjectsGalaxyFragment.newInstance(item);
        bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public static BottomSheetProjectsGalaxyFragment newInstance(ProjectDataIntra projectData) {
        BottomSheetProjectsGalaxyFragment fragment = new BottomSheetProjectsGalaxyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT, ServiceGenerator.getGson().toJson(projectData));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            projectData = ServiceGenerator.getGson().fromJson(getArguments().getString(ARG_PROJECT), ProjectDataIntra.class);
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_project_galaxy, null);
        dialog.setContentView(contentView);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        appClass = (AppClass) getActivity().getApplication();
        api = appClass.getApiService();

        TextView textViewTitle = (TextView) contentView.findViewById(R.id.textViewTitle);
        TextView textViewInfo = (TextView) contentView.findViewById(R.id.textViewInfo);
        TextView textViewRules = (TextView) contentView.findViewById(R.id.textViewRules);
        LinearLayout linearLayoutRules = (LinearLayout) contentView.findViewById(R.id.linearLayoutRules);
        TextView textViewState = (TextView) contentView.findViewById(R.id.textViewState);
        TextView textViewDescription = (TextView) contentView.findViewById(R.id.textViewDescription);
        buttonOpen = (ImageButton) contentView.findViewById(R.id.buttonOpen);

        buttonOpen.setOnClickListener(this);
        textViewTitle.setText(projectData.name);

        String state;
        if (projectData.state == null)
            state = getString(R.string.unknown_state);
        else if (projectData.state == ProjectDataIntra.State.DONE)
            state = getString(R.string.succeeded);
        else if (projectData.state == ProjectDataIntra.State.FAIL)
            state = getString(R.string.failed);
        else if (projectData.state == ProjectDataIntra.State.IN_PROGRESS)
            state = getString(R.string.in_progress);
        else if (projectData.state == ProjectDataIntra.State.AVAILABLE)
            state = getString(R.string.available);
        else if (projectData.state == ProjectDataIntra.State.UNAVAILABLE)
            state = getString(R.string.unavailable);
        else
            state = projectData.state.toString();

        if (!state.isEmpty() && projectData.finalMark != null)
            state += " • ";
        if (projectData.finalMark != null)
            state += projectData.finalMark;
        textViewState.setText(state);

        String info = projectData.difficulty;
        if (projectData.duration != null && !projectData.duration.isEmpty())
            info += " • " + projectData.duration;
        textViewInfo.setText(info);

        if (projectData.rules != null && !projectData.rules.isEmpty()) {
            linearLayoutRules.setVisibility(View.VISIBLE);
            textViewRules.setText(projectData.rules);
        } else
            linearLayoutRules.setVisibility(View.GONE);

        textViewDescription.setText(projectData.description);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        if (v == buttonOpen)
            ProjectActivity.openIt(getContext(), projectData.slug);
    }

}