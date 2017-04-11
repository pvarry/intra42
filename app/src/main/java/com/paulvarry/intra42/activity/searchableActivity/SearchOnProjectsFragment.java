package com.paulvarry.intra42.activity.searchableActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.paulvarry.intra42.Adapter.ListAdapterProjects;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.activity.project.ProjectActivity;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.CursusUsers;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.ui.BasicFragmentCall;

import java.util.List;

import retrofit2.Call;

public class SearchOnProjectsFragment extends BasicFragmentCall<Projects, ListAdapterProjects> {

    private final static String ARG = "searchOn";
    private String searchOn;

    public static SearchOnProjectsFragment newInstance(String searchOn) {

        Bundle args = new Bundle();

        args.putString(ARG, searchOn);

        SearchOnProjectsFragment fragment = new SearchOnProjectsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            searchOn = getArguments().getString(ARG);
        }
    }

    /**
     * Return a Call of retrofit2 can be enqueue()
     *
     * @param apiService Service
     * @param list       The current item on list (for pagination)
     * @return The Call
     */
    @Nullable
    @Override
    public Call<List<Projects>> getCall(ApiService apiService, @Nullable List<Projects> list) {
        AppClass app = (AppClass) getActivity().getApplication();
        CursusUsers cursusUsers = app.me.getCursusUsersToDisplay(getContext());

        Call<List<Projects>> callProjects;
        if (cursusUsers != null)
            callProjects = apiService.getProjectsSearch(cursusUsers.cursusId, searchOn);
        else
            callProjects = apiService.getProjectsSearch(searchOn);
        return callProjects;
    }

    /**
     * Get the message displayed when list view is empty
     *
     * @return The Message.
     */
    @Override
    public String getEmptyMessage() {
        return null;
    }

    /**
     * Generate a new adapter for the list, called on create fragment and after refresh.
     *
     * @param list The list for the ListViewAdapter
     * @return A Adapter.
     */
    @Override
    public ListAdapterProjects generateAdapter(List<Projects> list) {
        return new ListAdapterProjects(getActivity(), list);
    }

    /**
     * When a item on the list is clicked
     *
     * @param item The item clicked
     */
    @Override
    public void onItemClick(Projects item) {
        ProjectActivity.openIt(getContext(), item);
    }
}
