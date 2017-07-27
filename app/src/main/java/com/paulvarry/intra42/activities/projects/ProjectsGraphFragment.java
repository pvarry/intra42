package com.paulvarry.intra42.activities.projects;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.ApiServiceAuthServer;
import com.paulvarry.intra42.api.model.ProjectDataIntra;
import com.paulvarry.intra42.bottomSheet.BottomSheetProjectsGalaxyFragment;
import com.paulvarry.intra42.ui.Galaxy;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.GalaxyUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectsGraphFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectsGraphFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectsGraphFragment extends Fragment implements Galaxy.OnProjectClickListener {

    ProjectsActivity activity;
    private OnFragmentInteractionListener mListener;

    public ProjectsGraphFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectsGraphFragment.
     */
    public static ProjectsGraphFragment newInstance() {
        return new ProjectsGraphFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (ProjectsActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_projects_graph, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final AppClass app = activity.app;

        final Galaxy galaxy = view.findViewById(R.id.galaxy);
        galaxy.setOnProjectClickListener(this);

        ApiServiceAuthServer client = app.getApiServiceAuthServer();
        Call<List<ProjectDataIntra>> l = client.getGalaxy(AppSettings.getUserCursus(app), AppSettings.getUserCampus(app), app.me.login);
        l.enqueue(new Callback<List<ProjectDataIntra>>() {
            @Override
            public void onResponse(Call<List<ProjectDataIntra>> call, Response<List<ProjectDataIntra>> response) {
                if (response.isSuccessful())
                    galaxy.setData(response.body());
                else {
                    Toast.makeText(activity, "Unable to get live data for Galaxy", Toast.LENGTH_SHORT).show();
                    List<ProjectDataIntra> list = GalaxyUtils.getDataFromApp(getContext(), AppSettings.getUserCursus(app), AppSettings.getUserCampus(app), app.me);
                    galaxy.setData(list);
                }
            }

            @Override
            public void onFailure(Call<List<ProjectDataIntra>> call, Throwable t) {
                Toast.makeText(activity, "Unable to get live data for Galaxy", Toast.LENGTH_SHORT).show();
                List<ProjectDataIntra> list = GalaxyUtils.getDataFromApp(getContext(), AppSettings.getUserCursus(app), AppSettings.getUserCampus(app), app.me);
                galaxy.setData(list);
            }
        });
        galaxy.setState("loading");
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onClick(ProjectDataIntra projectData) {
        BottomSheetProjectsGalaxyFragment.openIt(activity, projectData);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
