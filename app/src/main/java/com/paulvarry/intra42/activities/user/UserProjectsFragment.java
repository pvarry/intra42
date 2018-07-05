package com.paulvarry.intra42.activities.user;

import android.animation.Animator;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.adapters.ListAdapterMarks;
import com.paulvarry.intra42.api.ApiServiceAuthServer;
import com.paulvarry.intra42.api.model.ProjectDataIntra;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.bottomSheet.BottomSheetProjectsGalaxyFragment;
import com.paulvarry.intra42.ui.Galaxy;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.GalaxyUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserProjectsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserProjectsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProjectsFragment
        extends Fragment
        implements Galaxy.OnProjectClickListener, AdapterView.OnItemSelectedListener,
        AdapterView.OnItemClickListener {

    private UserActivity activity;

    private ListView listView;
    private ListView listViewAll;
    private TextView textViewNoItem;
    private Galaxy galaxy;
    private int spinnerSelected = 0;
    private ListAdapterMarks adapterList;
    private Spinner spinnerContent;
    private OnFragmentInteractionListener mListener;

    public UserProjectsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserProjectsFragment.
     */
    public static UserProjectsFragment newInstance() {
        return new UserProjectsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (UserActivity) getActivity();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_projects, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (UserActivity) getActivity();

        listView = view.findViewById(R.id.listView);
        listViewAll = view.findViewById(R.id.listViewAll);
        textViewNoItem = view.findViewById(R.id.textViewNoItem);
        galaxy = view.findViewById(R.id.galaxy);
        spinnerContent = view.findViewById(R.id.spinnerContent);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(activity, R.array.spinner_user_projects, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spinnerContent.setAdapter(adapter);
        spinnerContent.setOnItemSelectedListener(this);

        setViewHide();
        galaxy.setVisibility(View.VISIBLE);

        galaxy.setOnProjectClickListener(this);
        listView.setOnItemClickListener(this);
        listViewAll.setOnItemClickListener(this);

        if (activity != null && activity.user != null) {
            if (activity.selectedCursus == null)
                activity.selectedCursus = activity.user.getCursusUsersToDisplay(getContext());
            if (activity.selectedCursus == null)
                return;

            ApiServiceAuthServer client = activity.app.getApiServiceAuthServer();
            Call<List<ProjectDataIntra>> call = client.getGalaxy(activity.selectedCursus.cursusId, AppSettings.getUserCampus(activity.app), activity.user.login);
            call.enqueue(new Callback<List<ProjectDataIntra>>() {
                @Override
                public void onResponse(Call<List<ProjectDataIntra>> call, Response<List<ProjectDataIntra>> response) {
                    if (response.isSuccessful())
                        galaxy.setData(response.body());
                    else {
                        setGalaxyNoData();
                    }
                }

                @Override
                public void onFailure(Call<List<ProjectDataIntra>> call, Throwable t) {
                    t.printStackTrace();
                    setGalaxyNoData();
                }
            });
            galaxy.setState(getString(R.string.info_loading));
        }
    }

    void setGalaxyNoData() {
        if (!isAdded() || isDetached()) return;
        Toast.makeText(activity, R.string.galaxy_no_live_data, Toast.LENGTH_SHORT).show();
        List<ProjectDataIntra> list = GalaxyUtils.getDataFromApp(activity, activity.selectedCursus.cursusId, AppSettings.getUserCampus(activity.app), activity.user);
        galaxy.setData(list);
    }

    void setViewHide() {
        galaxy.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);
        listViewAll.setVisibility(View.GONE);
        textViewNoItem.setVisibility(View.GONE);
    }


    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void animate(View action, View view) {

        view.bringToFront();
        view.setVisibility(View.VISIBLE);

        if (action == null)
            return;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {

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
    public void onClick(ProjectDataIntra projectData) {
        BottomSheetProjectsGalaxyFragment.openIt(requireActivity(), projectData, activity.user.id);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {

        if (activity == null)
            return;
        if (spinnerSelected == position)
            return;
        spinnerSelected = position;
        if (position == 0) { // show galaxy
            List<ProjectDataIntra> list = GalaxyUtils.getDataFromApp(activity, activity.selectedCursus.cursusId, AppSettings.getUserCampus(activity.app), activity.user);
            galaxy.setData(list);
            animate(spinnerContent, galaxy);
        } else {
            List<ProjectsUsers> list;

            if (position == 1) { // show all project list
                if (activity.user != null) list = activity.user.projectsUsers;
                else list = new ArrayList<>();
                list = ProjectsUsers.getListOnlyRoot(list);
                if (activity.selectedCursus != null)
                    list = ProjectsUsers.getListCursus(list, activity.selectedCursus.cursusId);
                if (list.isEmpty())
                    animate(spinnerContent, textViewNoItem);
                else {
                    adapterList = new ListAdapterMarks(activity, list);
                    listViewAll.setAdapter(adapterList);
                    animate(spinnerContent, listViewAll);
                }
            } else if (position == 2) { // show in-progress list
                if (activity.user != null)
                    list = ProjectsUsers.getListCursusDoing(activity.user.projectsUsers, activity.selectedCursus);
                else list = new ArrayList<>();
                if (list.isEmpty())
                    animate(spinnerContent, textViewNoItem);
                else {
                    adapterList = new ListAdapterMarks(activity, list);
                    listView.setAdapter(adapterList);
                    animate(spinnerContent, listView);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ProjectActivity.openIt(getContext(), adapterList.getItem(position), activity.user);
    }

    public boolean canSwipe() {
        return spinnerContent != null && spinnerContent.getSelectedItemPosition() != 0;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
