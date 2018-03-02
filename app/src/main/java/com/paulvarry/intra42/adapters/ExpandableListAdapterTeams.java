package com.paulvarry.intra42.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.paolorotolo.expandableheightlistview.ExpandableHeightGridView;
import com.github.paolorotolo.expandableheightlistview.ExpandableHeightListView;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.user.UserActivity;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.api.model.Teams;
import com.paulvarry.intra42.utils.DateTool;
import com.paulvarry.intra42.utils.ProjectUserStatus;

import java.util.ArrayList;
import java.util.List;

public class ExpandableListAdapterTeams extends BaseExpandableListAdapter {

    private final Activity context;
    private List<Teams> teamsList;

    public ExpandableListAdapterTeams(Activity context, List<Teams> teamsList) {

        this.context = context;
        this.teamsList = teamsList;
    }


    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return teamsList.size();
    }

    /**
     * Gets the number of children in a specified group.
     *
     * @param groupPosition the position of the group for which the children
     *                      count should be returned
     * @return the children count in the specified group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    /**
     * Gets the data associated with the given group.
     *
     * @param groupPosition the position of the group
     * @return the data child for the specified group
     */
    @Override
    public Teams getGroup(int groupPosition) {
        return teamsList.get(groupPosition);
    }

    /**
     * Gets the data associated with the given child within the given group.
     *
     * @param groupPosition the position of the group that the child resides in
     * @param childPosition the position of the child with respect to other
     *                      children in the group
     * @return the data of the child
     */
    @Override
    public Teams getChild(int groupPosition, int childPosition) {
        return teamsList.get(groupPosition);
    }

    /**
     * Gets the ID for the group at the given position. This group ID must be
     * unique across groups. The combined ID (see
     * {@link #getCombinedGroupId(long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group for which the ID is wanted
     * @return the ID associated with the group
     */
    @Override
    public long getGroupId(int groupPosition) {
        return teamsList.get(groupPosition).id;
    }

    /**
     * Gets the ID for the given child within the given group. This ID must be
     * unique across all children within the group. The combined ID (see
     * {@link #getCombinedChildId(long, long)}) must be unique across ALL items
     * (groups and all children).
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group for which
     *                      the ID is wanted
     * @return the ID associated with the child
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return teamsList.get(groupPosition).id;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
     * //     * @see Adapter#hasStableIds()
     */
    @Override
    public boolean hasStableIds() {
        return false;
    }

    /**
     * Gets a View that displays the given group. This View is only for the
     * group--the Views for the group's children will be fetched using
     * {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     *
     * @param groupPosition the position of the group for which the View is
     *                      returned
     * @param isExpanded    whether the group is expanded or collapsed
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final Teams teams = getGroup(groupPosition);
        ViewHolderGroup holder;

        if (convertView == null) {
            holder = new ViewHolderGroup();

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.expandable_list_view_teams_group, parent, false);

            holder.textViewNameGroup = convertView.findViewById(R.id.textViewNameGroup);
            holder.textViewMark = convertView.findViewById(R.id.textViewMark);

            convertView.setTag(holder);
        } else
            holder = (ViewHolderGroup) convertView.getTag();

        holder.textViewNameGroup.setText(teams.name);
        ProjectUserStatus.setMark(context, teams, holder.textViewMark);

        return convertView;
    }

    /**
     * Gets a View that displays the data for the given child within the given
     * group.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child (for which the View is
     *                      returned) within the group
     * @param isLastChild   Whether the child is the last child within the group
     * @param convertView   the old view to reuse, if possible. You should check
     *                      that this view is non-null and of an appropriate type before
     *                      using. If it is not possible to convert this view to display
     *                      the correct data, this method can create a new view. It is not
     *                      guaranteed that the convertView will have been previously
     *                      created.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final Teams team = getChild(groupPosition, childPosition);
        ViewHolderChild holder;

        if (convertView == null) {
            holder = new ViewHolderChild();
            LayoutInflater layoutInflater = LayoutInflater.from(this.context);
            convertView = layoutInflater.inflate(R.layout.expandable_list_view_teams_item, parent, false);

            holder.textViewStatus = convertView.findViewById(R.id.textViewStatus);
            holder.textViewCaptionGitRepository = convertView.findViewById(R.id.textViewCaptionGitRepository);
            holder.textViewCaptionAutomaticEvaluations = convertView.findViewById(R.id.textViewCaptionAutomaticEvaluations);
            holder.textViewGit = convertView.findViewById(R.id.textViewGit);
            holder.imageButtonCopyGit = convertView.findViewById(R.id.imageButtonCopyGit);
            holder.expandableHeightGridViewUsers = convertView.findViewById(R.id.expandableHeightGridViewUsers);
            holder.expandableHeightListViewPeerCorrections = convertView.findViewById(R.id.expandableHeightListViewPeerCorrections);
            holder.textViewCaptionPeerCorrection = convertView.findViewById(R.id.textViewCaptionPeerCorrection);
            holder.expandableHeightListViewAutomaticCorrections = convertView.findViewById(R.id.expandableHeightListViewAutomaticCorrections);
            holder.viewGroupGitRepository = convertView.findViewById(R.id.viewGroupGitRepository);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }

        StringBuilder str = new StringBuilder();
        if (team.closed) {
            str.append(context.getString(R.string.project_team_status_closed));
            if (team.closedAt != null)
                str.append(" ").append(DateTool.getDurationAgo(team.closedAt));
        } else if (team.locked) {
            str.append(context.getString(R.string.project_team_status_locked));
            if (team.lockedAt != null)
                str.append(" ").append(DateTool.getDurationAgo(team.lockedAt));
        } else
            holder.textViewStatus.setVisibility(View.GONE);
        holder.textViewStatus.setText(str);

        if (team.repoUrl == null) {
            holder.textViewCaptionGitRepository.setVisibility(View.GONE);
            holder.viewGroupGitRepository.setVisibility(View.GONE);
        } else {
            holder.textViewCaptionGitRepository.setVisibility(View.VISIBLE);
            holder.viewGroupGitRepository.setVisibility(View.VISIBLE);

            holder.textViewGit.setText(team.repoUrl);
            holder.imageButtonCopyGit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    if (clipboard == null)
                        return;
                    ClipData clip = ClipData.newPlainText("label", team.repoUrl);
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show();
                }
            });
        }

        GridAdapterUsersLittle adapterUsers = new GridAdapterUsersLittle(context, team.users);
        holder.expandableHeightGridViewUsers.setAdapter(adapterUsers);
        holder.expandableHeightGridViewUsers.setExpanded(true);
        holder.expandableHeightGridViewUsers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (team.users != null && team.users.get(position) != null)
                    Toast.makeText(context, team.users.get(position).login, Toast.LENGTH_SHORT).show();
            }
        });
        holder.expandableHeightGridViewUsers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (team.users != null && team.users.get(position) != null) {
                    UserActivity.openIt(context, team.users.get(position));
                    return true;
                }
                return false;
            }
        });

        if (team.teamsUploads == null || team.teamsUploads.isEmpty()) {
            holder.textViewCaptionAutomaticEvaluations.setVisibility(View.GONE);
            holder.expandableHeightListViewAutomaticCorrections.setVisibility(View.GONE);
        } else {
            holder.textViewCaptionAutomaticEvaluations.setVisibility(View.VISIBLE);
            holder.expandableHeightListViewAutomaticCorrections.setVisibility(View.VISIBLE);

            ListAdapterScaleTeamsAutomatic adapterAutoScale = new ListAdapterScaleTeamsAutomatic(context, team.teamsUploads);
            holder.expandableHeightListViewAutomaticCorrections.setExpanded(true);
            holder.expandableHeightListViewAutomaticCorrections.setAdapter(adapterAutoScale);
        }

        // set-up valid scale team
        String peer_corrections = context.getResources().getString(R.string.project_peer_corrections);
        final List<ScaleTeams> tmpScaleTeams = new ArrayList<>();
        if (team.scaleTeams != null && !team.scaleTeams.isEmpty()) {
            for (ScaleTeams s : team.scaleTeams) {
                if (s.corrector != null && s.beginAt != null && DateTool.isInPast(s.beginAt) && s.comment != null)
                    tmpScaleTeams.add(s);
            }
        }

        if (team.scaleTeams == null || team.scaleTeams.isEmpty() || tmpScaleTeams.isEmpty()) {
            holder.textViewCaptionPeerCorrection.setVisibility(View.GONE);
            holder.expandableHeightListViewPeerCorrections.setVisibility(View.GONE);
        } else {
            holder.textViewCaptionPeerCorrection.setVisibility(View.VISIBLE);
            holder.expandableHeightListViewPeerCorrections.setVisibility(View.VISIBLE);

            ScaleTeams scaleTeamsForScale = team.scaleTeams.get(0);
            if (scaleTeamsForScale != null && scaleTeamsForScale.scale != null)
                peer_corrections += " (" + String.valueOf(tmpScaleTeams.size()) + "/" + scaleTeamsForScale.scale.correctionNumber + ")";
            holder.textViewCaptionPeerCorrection.setText(peer_corrections);

            ListAdapterScaleTeams adapterScaleTeams = new ListAdapterScaleTeams(context, tmpScaleTeams);
            holder.expandableHeightListViewPeerCorrections.setExpanded(true);
            holder.expandableHeightListViewPeerCorrections.setAdapter(adapterScaleTeams);
            holder.expandableHeightListViewPeerCorrections.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (tmpScaleTeams.get(position) != null) {
                        UserActivity.openIt(context, tmpScaleTeams.get(position).corrector);
                        return true;
                    }
                    return false;
                }
            });
        }

        return convertView;
    }

    /**
     * Whether the child at the specified position is selectable.
     *
     * @param groupPosition the position of the group that contains the child
     * @param childPosition the position of the child within the group
     * @return whether the child is selectable.
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private static class ViewHolderChild {

        private TextView textViewCaptionPeerCorrection;
        private TextView textViewStatus;
        private TextView textViewCaptionGitRepository;
        private TextView textViewCaptionAutomaticEvaluations;
        private TextView textViewGit;
        private ImageButton imageButtonCopyGit;
        private ExpandableHeightGridView expandableHeightGridViewUsers;
        private ExpandableHeightListView expandableHeightListViewAutomaticCorrections;
        private ExpandableHeightListView expandableHeightListViewPeerCorrections;
        private ViewGroup viewGroupGitRepository;

    }

    private static class ViewHolderGroup {

        private TextView textViewNameGroup;
        private TextView textViewMark;

    }
}
