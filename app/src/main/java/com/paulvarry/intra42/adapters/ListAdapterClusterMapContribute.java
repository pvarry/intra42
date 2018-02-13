package com.paulvarry.intra42.adapters;


import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.ClusterMapContributeEditActivity;
import com.paulvarry.intra42.utils.clusterMap.Firebase.Cluster;

import java.util.List;

public class ListAdapterClusterMapContribute extends BaseExpandableListAdapter {

    private final Context context;
    private List<Cluster> clusterList;

    public ListAdapterClusterMapContribute(Context context, List<Cluster> clusterList) {

        this.context = context;
        this.clusterList = clusterList;
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return clusterList.size();
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
    public Cluster getGroup(int groupPosition) {
        return clusterList.get(groupPosition);
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
    public Cluster getChild(int groupPosition, int childPosition) {
        return getGroup(groupPosition);
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
        return getGroup(groupPosition).campusId;
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
        return -1;
    }

    /**
     * Indicates whether the child and group IDs are stable across changes to the
     * underlying data.
     *
     * @return whether or not the same ID always refers to the same object
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
     *                      created by
     *                      {@link #getGroupView(int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the group at the specified position
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        final ViewHolderGroup holder;

        if (convertView == null) {
            holder = new ViewHolderGroup();

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_view_cluster_map_contribute_list_group, parent, false);

            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }

        Cluster item = getGroup(groupPosition);

        holder.textViewTitle.setText(item.name);

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
     *                      created by
     *                      {@link #getChildView(int, int, boolean, View, ViewGroup)}.
     * @param parent        the parent that this view will eventually be attached to
     * @return the View corresponding to the child at the specified position
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolderChild holder;

        if (convertView == null) {
            holder = new ViewHolderChild();

            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_view_cluster_map_contribute_list_child, parent, false);

            holder.textViewLocked = convertView.findViewById(R.id.textViewLocked);
            holder.buttonEditLayout = convertView.findViewById(R.id.buttonEditLayout);
            holder.buttonEditMetadata = convertView.findViewById(R.id.buttonEditMetadata);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }

        final Cluster item = getGroup(groupPosition);

        if (item.hostPrefix.contentEquals("e1")) { // just for testing
            holder.buttonEditLayout.setEnabled(false);
            holder.buttonEditMetadata.setEnabled(false);
            holder.textViewLocked.setVisibility(View.VISIBLE);
        } else {
            holder.buttonEditLayout.setEnabled(true);
            holder.buttonEditMetadata.setEnabled(true);
            holder.textViewLocked.setVisibility(View.GONE);
        }

        // holder.textViewLocked.setText();
        holder.buttonEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Opening in progress", Toast.LENGTH_SHORT).show();
                ClusterMapContributeEditActivity.openIt(context, item.hostPrefix, item.campusId);
            }
        });

        holder.buttonEditMetadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater inflater = LayoutInflater.from(context);
                final View view = inflater.inflate(R.layout.list_view_cluster_map_contribute_cluster, null);
                final EditText editTextPrefix = view.findViewById(R.id.editTextPrefix);
                final EditText editTextCampus = view.findViewById(R.id.editTextCampus);

                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                editTextPrefix.setText(item.hostPrefix);
                editTextCampus.setText(String.valueOf(item.campusId));

                alert.setTitle("Edit cluster metadata");
                alert.setView(view);
                alert.setPositiveButton("save", null);
                alert.show();
            }
        });

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


    private static class ViewHolderGroup {

        TextView textViewTitle;
    }

    private static class ViewHolderChild {

        TextView textViewLocked;
        Button buttonEditLayout;
        Button buttonEditMetadata;
    }
}