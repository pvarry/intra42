package com.paulvarry.intra42.bottomSheet;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.ServiceGenerator;
import com.paulvarry.intra42.api.model.ProjectDataIntra;
import com.paulvarry.intra42.ui.ListenedBottomSheetDialogFragment;

public class BottomSheetProjectsGalaxyFragment extends ListenedBottomSheetDialogFragment implements View.OnClickListener {

    private static final String ARG_PROJECT = "project_galaxy";
    private static final String ARG_USER = "id_user";

    ImageButton buttonOpen;

    AppClass appClass;
    ApiService api;

    ProjectDataIntra projectData;
    int idUser;

    public static void openIt(FragmentActivity activity, ProjectDataIntra item, int idUser) {
        BottomSheetProjectsGalaxyFragment bottomSheetDialogFragment = BottomSheetProjectsGalaxyFragment.newInstance(item, idUser);
        bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public static void openIt(FragmentActivity activity, ProjectDataIntra item) {
        BottomSheetProjectsGalaxyFragment bottomSheetDialogFragment = BottomSheetProjectsGalaxyFragment.newInstance(item, 0);
        bottomSheetDialogFragment.show(activity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
    }

    public static BottomSheetProjectsGalaxyFragment newInstance(ProjectDataIntra projectData, int idUser) {
        BottomSheetProjectsGalaxyFragment fragment = new BottomSheetProjectsGalaxyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PROJECT, ServiceGenerator.getGson().toJson(projectData));
        args.putInt(ARG_USER, idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = getArguments();
        if (bundle != null) {
            projectData = ServiceGenerator.getGson().fromJson(bundle.getString(ARG_PROJECT), ProjectDataIntra.class);
            idUser = bundle.getInt(ARG_USER);
        }
    }

    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        View contentView = View.inflate(getContext(), R.layout.fragment_bottom_sheet_project_galaxy, null);
        dialog.setContentView(contentView);

        appClass = (AppClass) getActivity().getApplication();
        api = appClass.getApiService();

        TextView textViewTitle = contentView.findViewById(R.id.textViewTitle);
        TextView textViewInfo = contentView.findViewById(R.id.textViewInfo);
        TextView textViewRules = contentView.findViewById(R.id.textViewRules);
        LinearLayout linearLayoutRules = contentView.findViewById(R.id.linearLayoutRules);
        TextView textViewState = contentView.findViewById(R.id.textViewState);
        TextView textViewDescription = contentView.findViewById(R.id.textViewDescription);
        buttonOpen = contentView.findViewById(R.id.buttonOpen);

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
            ProjectActivity.openIt(getContext(), projectData.slug, idUser);
    }

}