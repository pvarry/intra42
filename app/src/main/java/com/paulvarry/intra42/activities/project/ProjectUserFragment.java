package com.paulvarry.intra42.activities.project;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.adapters.RecyclerAdapterScaleTeams;
import com.paulvarry.intra42.adapters.RecyclerAdapterScaleTeamsAutomatic;
import com.paulvarry.intra42.adapters.RecyclerAdapterUserTeam;
import com.paulvarry.intra42.adapters.SpinnerAdapterTeams;
import com.paulvarry.intra42.api.ApiService;
import com.paulvarry.intra42.api.model.*;
import com.paulvarry.intra42.ui.BasicFragmentSpinner;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.Tools;
import retrofit2.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProjectUserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProjectUserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProjectUserFragment extends BasicFragmentSpinner<Teams, SpinnerAdapterTeams> {

    private Teams team;

    private TextView textViewCaptionPeerCorrection;
    private TextView textViewStatus;
    private TextView textViewCaptionGitRepository;
    private TextView textViewCaptionAutomaticEvaluations;
    private TextView textViewGit;
    private ImageButton imageButtonCopyGit;
    private RecyclerView recyclerViewUsers;
    private RecyclerView recyclerViewAutomaticCorrections;
    private RecyclerView recyclerViewPeerCorrections;
    private ViewGroup viewGroupGitRepository;
    private ProgressBar progressBar;

    @Nullable
    private OnFragmentInteractionListener mListener;

    public ProjectUserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment ProjectUserFragment.
     */
    public static ProjectUserFragment newInstance() {
        return new ProjectUserFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateChildView(@NonNull LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_project_user, container, false);

        textViewStatus = view.findViewById(R.id.textViewStatus);
        textViewCaptionGitRepository = view.findViewById(R.id.textViewCaptionGitRepository);
        textViewCaptionAutomaticEvaluations = view.findViewById(R.id.textViewCaptionAutomaticEvaluations);
        textViewGit = view.findViewById(R.id.textViewGit);
        imageButtonCopyGit = view.findViewById(R.id.imageButtonCopyGit);
        recyclerViewUsers = view.findViewById(R.id.recyclerViewUsers);
        recyclerViewPeerCorrections = view.findViewById(R.id.recyclerViewPeerCorrections);
        textViewCaptionPeerCorrection = view.findViewById(R.id.textViewCaptionPeerCorrection);
        recyclerViewAutomaticCorrections = view.findViewById(R.id.recyclerViewAutomaticCorrections);
        viewGroupGitRepository = view.findViewById(R.id.viewGroupGitRepository);
        progressBar = view.findViewById(R.id.progressBar);

        return view;
    }

    @Override
    public void onHeaderItemChanged(final Teams team) {
        final Context context = getContext();
        this.team = team;

        StringBuilder str = new StringBuilder();
        if (team.closed) {
            str.append(getString(R.string.project_team_status_closed));
            if (team.closedAt != null)
                str.append(" ").append(DateTool.getDurationAgo(team.closedAt));
        } else if (team.locked) {
            str.append(getString(R.string.project_team_status_locked));
            if (team.lockedAt != null)
                str.append(" ").append(DateTool.getDurationAgo(team.lockedAt));
        } else
            textViewStatus.setVisibility(View.GONE);
        textViewStatus.setText(str);

        if (team.repoUrl == null) {
            textViewCaptionGitRepository.setVisibility(View.GONE);
            viewGroupGitRepository.setVisibility(View.GONE);
        } else {
            textViewCaptionGitRepository.setVisibility(View.VISIBLE);
            viewGroupGitRepository.setVisibility(View.VISIBLE);

            textViewGit.setText(team.repoUrl);
            imageButtonCopyGit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (context == null)
                        return;
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard == null)
                        return;
                    ClipData clip = ClipData.newPlainText("label", team.repoUrl);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
                }
            });
        }

        RecyclerAdapterUserTeam adapterUsers = new RecyclerAdapterUserTeam(context, team.users);
        recyclerViewUsers.setAdapter(adapterUsers);
        recyclerViewUsers.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        adapterUsers.setOnItemClickListener(new RecyclerAdapterUserTeam.OnItemClickListener() {
            @Override
            public void onItemTeamUserClick(int position, TeamsUsers users) {
                if (team.users != null && team.users.get(position) != null) {
                    TeamsUsers user = team.users.get(position);
                    if (user.leader)
                        Toast.makeText(context, getString(R.string.project_team_users_toast_leader, team.users.get(position).login), Toast.LENGTH_LONG).show();
                    else
                        Toast.makeText(context, getString(R.string.project_team_users_toast, team.users.get(position).login), Toast.LENGTH_LONG).show();

                }
            }
        });
        adapterUsers.setOnItemLongClickListener(new RecyclerAdapterUserTeam.OnItemLongClickListener() {
            @Override
            public boolean onItemTeamUserLongClick(int position, TeamsUsers users) {
                if (team.users != null && team.users.get(position) != null) {
                    actionForUser(context, team.users.get(position));
                    return true;
                }
                return false;
            }
        });

        textViewCaptionAutomaticEvaluations.setVisibility(View.GONE);
        recyclerViewAutomaticCorrections.setVisibility(View.GONE);
        textViewCaptionPeerCorrection.setVisibility(View.GONE);
        recyclerViewPeerCorrections.setVisibility(View.GONE);

        if (team.extraAdded) {
            setViewScaleTeam(team);
            progressBar.setVisibility(View.GONE);
        } else {
            loadExtraDataForTeam();
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    void setViewScaleTeam(Teams team) {
        final Context context = getContext();
        if (context == null || !isAdded())
            return;

        progressBar.setVisibility(View.GONE);

        if (team.teamsUploads == null || team.teamsUploads.isEmpty()) {
            textViewCaptionAutomaticEvaluations.setVisibility(View.GONE);
            recyclerViewAutomaticCorrections.setVisibility(View.GONE);
        } else {
            textViewCaptionAutomaticEvaluations.setVisibility(View.VISIBLE);
            recyclerViewAutomaticCorrections.setVisibility(View.VISIBLE);

            RecyclerAdapterScaleTeamsAutomatic adapterAutoScale = new RecyclerAdapterScaleTeamsAutomatic(team.teamsUploads);
            recyclerViewAutomaticCorrections.setAdapter(adapterAutoScale);
            recyclerViewAutomaticCorrections.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewAutomaticCorrections.setNestedScrollingEnabled(false);
        }

        // set-up valid scale team
        String peer_corrections = getResources().getString(R.string.project_peer_corrections);
        final List<ScaleTeams> tmpScaleTeams = new ArrayList<>();
        if (team.scaleTeams != null && !team.scaleTeams.isEmpty()) {
            for (ScaleTeams s : team.scaleTeams) {
                if (s.corrector != null && s.beginAt != null && DateTool.isInPast(s.beginAt) && s.comment != null)
                    tmpScaleTeams.add(s);
            }
        }

        // set peer evaluations list
        if (team.scaleTeams == null || team.scaleTeams.isEmpty() || tmpScaleTeams.isEmpty()) {
            textViewCaptionPeerCorrection.setVisibility(View.GONE);
            recyclerViewPeerCorrections.setVisibility(View.GONE);
        } else {
            textViewCaptionPeerCorrection.setVisibility(View.VISIBLE);
            recyclerViewPeerCorrections.setVisibility(View.VISIBLE);
            recyclerViewPeerCorrections.setLayoutManager(new LinearLayoutManager(context));

            ScaleTeams scaleTeamsForScale = team.scaleTeams.get(0);
            if (scaleTeamsForScale != null && scaleTeamsForScale.scale != null)
                peer_corrections += " (" + String.valueOf(tmpScaleTeams.size()) + "/" + scaleTeamsForScale.scale.correctionNumber + ")";
            textViewCaptionPeerCorrection.setText(peer_corrections);

            RecyclerAdapterScaleTeams adapterScaleTeams = new RecyclerAdapterScaleTeams(tmpScaleTeams);
            recyclerViewPeerCorrections.setAdapter(adapterScaleTeams);
            recyclerViewPeerCorrections.setLayoutManager(new LinearLayoutManager(context));
            recyclerViewPeerCorrections.setNestedScrollingEnabled(false);
            if (recyclerViewPeerCorrections.getItemDecorationCount() == 0) {
                DividerItemDecoration itemDecoration = new DividerItemDecoration(context, DividerItemDecoration.VERTICAL);
                itemDecoration.setDrawable(getResources().getDrawable(R.drawable.line_divider_tiny));
                recyclerViewPeerCorrections.addItemDecoration(itemDecoration);
            }
            adapterScaleTeams.setOnItemClickListener(new RecyclerAdapterScaleTeams.OnItemClickListener() {
                @Override
                public void onItemClicked(int position, final ScaleTeams scaleTeams) {
                    if (scaleTeams.corrector != null)
                        actionForUser(context, scaleTeams.corrector);
                }
            });
        }
    }

    void loadExtraDataForTeam() {
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FragmentActivity activity = getActivity();
                    if (activity == null || mListener == null)
                        return;
                    ApiService apiService = ((AppClass) (activity.getApplication())).getApiService();

                    loadDataProjectUser(apiService);

                    ProjectUserFragment.super.listSpinnerHeader.clear();
                    ProjectUserFragment.super.listSpinnerHeader.addAll(mListener.getData().user.teams);

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setViewScaleTeam(team);
                        }
                    });

                    if (team != null && team.scaleTeams != null) {
                        for (ScaleTeams s : team.scaleTeams) {
                            if (s.feedback == null)
                                continue;
                            Response<List<Feedback>> response = apiService.getFeedbacks(s.id).execute();
                            if (Tools.apiIsSuccessful(response))
                                s.feedbacks = response.body();
                        }
                        team.extraAdded = true;
                    }
                } catch (IOException | RuntimeException e) {
                    e.printStackTrace();
                }
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        setViewScaleTeam(team);
                    }
                });
            }
        }).start();
    }

    void loadDataProjectUser(ApiService apiService) throws IOException, RuntimeException { // load general data
        if (mListener == null)
            return;
        ProjectActivity.ProjectUser data = mListener.getData();
        if (!data.extraDataAdded) {
            ProjectActivity.ProjectUser.fillTeams(apiService, data);
            data.extraDataAdded = true;
            if (data.user.teams != null) {
                for (Teams t : data.user.teams) {
                    if (t.id == team.id) {
                        team = t;
                        break;
                    }
                }
            }
        }
    }

    private void actionForUser(final Context context, final UsersLTE user) {
        if (mListener == null)
            return;
        final Projects projectsLTE = mListener.getData().project;
        String action2 = context.getString(R.string.format_project_team_users_action_open).replace("{project}", projectsLTE.name).replace("{user}", user.login);
        String[] items = new String[]{context.getString(R.string.format__user_profile, user.login), action2};

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0)
                    UserActivity.openIt(context, user);
                else if (which == 1)
                    ProjectActivity.openIt(context, projectsLTE, user);
            }
        });
        builder.show();
    }

    @Nullable
    @Override
    public List<Teams> getSpinnerElemList() {
        if (mListener != null)
            return mListener.getData().user.teams;
        return null;
    }

    @Override
    public SpinnerAdapterTeams onGenerateHeaderAdapter(List<Teams> items) {
        return new SpinnerAdapterTeams(getContext(), items);
    }

    @Override
    public int getSpinnerDefaultPosition(List list) {
        return 0;
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
        ProjectActivity.ProjectUser getData();
    }
}
