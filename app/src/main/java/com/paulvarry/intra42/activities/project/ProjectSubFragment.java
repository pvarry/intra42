package com.paulvarry.intra42.activities.project;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.paulvarry.intra42.adapters.ListAdapterProjectsMarks;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsLTE;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.ui.BasicFragmentCall;
import retrofit2.Call;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectSubFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectSubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectSubFragment extends BasicFragmentCall<ProjectsUsers, ListAdapterProjectsMarks> {

    private ProjectActivity activity;
    private ListAdapterProjectsMarks listAdapterMarks;

    private OnFragmentInteractionListener mListener;

    public ProjectSubFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectSubFragment.
     */
    public static ProjectSubFragment newInstance() {
        return new ProjectSubFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ProjectActivity) getActivity();
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

    @Nullable
    @Override
    public Call<List<ProjectsUsers>> getCall(ApiService apiService, int page) {
        if (activity == null || activity.projectUser == null)
            return null;
        if (activity.projectUser.user == null || activity.projectUser.user.user == null) {
            this.list = new ArrayList<>();
            return null;
        }

        int start = 0;
        if (list != null)
            start = list.size();
        String filter = Projects.concatIds(activity.projectUser.project.children, start, 100);
        return apiService.getProjectsUsers(filter, activity.projectUser.user.user.id, 100, page);
    }

    @Override
    public String getEmptyMessage() {
        return null;
    }

    @Override
    public void onItemClick(ProjectsUsers item) {
        ProjectActivity.openIt(getContext(), item);
    }

    /**
     * Callback method to be invoked when an item in this AdapterView has
     * been clicked.
     * <p/>
     * Implementers can call getItemAtPosition(position) if they need
     * to access the data associated with the selected item.
     *
     * @param parent   The AdapterView where the click happened.
     * @param view     The view within the AdapterView that was clicked (this
     *                 will be a view provided by the adapter)
     * @param position The position of the view in the adapter.
     * @param id       The row id of the item that was clicked.
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (listAdapterMarks != null) {

            ProjectsLTE project = listAdapterMarks.getItem(position);
            ProjectsUsers projectsUser = listAdapterMarks.getProjectUser(project);

            if (projectsUser != null)
                ProjectActivity.openIt(getContext(), projectsUser);
            else if (activity != null && activity.projectUser != null && activity.projectUser.user != null && activity.projectUser.user.user != null)
                ProjectActivity.openIt(getContext(), project, activity.projectUser.user.user);
            else if (activity != null && activity.login != null)
                ProjectActivity.openIt(getContext(), project, activity.login);
            else if (activity != null)
                ProjectActivity.openIt(getContext(), project, activity.idUser);
            else
                ProjectActivity.openIt(getContext(), project);
        }
    }

    @Override
    public ListAdapterProjectsMarks generateAdapter(List<ProjectsUsers> list) {
        listAdapterMarks = new ListAdapterProjectsMarks(activity, activity.projectUser.project.children);
        listAdapterMarks.setProjectUser(list);
        return listAdapterMarks;
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
