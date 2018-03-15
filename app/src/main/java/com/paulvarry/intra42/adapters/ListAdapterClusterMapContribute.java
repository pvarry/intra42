package com.paulvarry.intra42.adapters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.paulvarry.intra42.AppClass;
import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.cluster_map_contribute.Master;
import com.paulvarry.intra42.utils.ClusterMapContributeUtils;
import com.paulvarry.intra42.utils.DateTool;

import java.util.Calendar;
import java.util.List;

public class ListAdapterClusterMapContribute extends BaseExpandableListAdapter {

    private final AppClass app;
    private List<Master> masterList;
    private OnEditClickListener listener;

    public ListAdapterClusterMapContribute(AppClass app, List<Master> clusterList) {

        this.app = app;
        this.masterList = clusterList;
    }

    public void setOnEditListener(OnEditClickListener listener) {
        this.listener = listener;
    }

    /**
     * Gets the number of groups.
     *
     * @return the number of groups
     */
    @Override
    public int getGroupCount() {
        return masterList.size();
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
    public Master getGroup(int groupPosition) {
        return masterList.get(groupPosition);
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
    public Master getChild(int groupPosition, int childPosition) {
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
        return -1;
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

            LayoutInflater inflater = LayoutInflater.from(app);
            convertView = inflater.inflate(R.layout.list_view_cluster_map_contribute_list_group, parent, false);

            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderGroup) convertView.getTag();
        }

        Master item = getGroup(groupPosition);

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
    public View getChildView(final int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ViewHolderChild holder;

        if (convertView == null) {
            holder = new ViewHolderChild();

            LayoutInflater inflater = LayoutInflater.from(app);
            convertView = inflater.inflate(R.layout.list_view_cluster_map_contribute_list_child, parent, false);

            holder.textViewLocked = convertView.findViewById(R.id.textViewLocked);
            holder.buttonEditLayout = convertView.findViewById(R.id.buttonEditLayout);
            holder.buttonEditMetadata = convertView.findViewById(R.id.buttonEditMetadata);

            convertView.setTag(holder);

        } else {
            holder = (ViewHolderChild) convertView.getTag();
        }

        final Master item = getGroup(groupPosition);

        if (ClusterMapContributeUtils.canIEdit(item, app)) {
            holder.buttonEditLayout.setEnabled(true);
            holder.buttonEditMetadata.setEnabled(true);
            holder.textViewLocked.setVisibility(View.GONE);
        } else {
            holder.buttonEditLayout.setEnabled(false);
            holder.buttonEditMetadata.setEnabled(false);

            String lockString = app.getString(R.string.cluster_map_contribute_locked_indicator);
            Calendar c = Calendar.getInstance();
            c.setTime(item.locked_at);
            c.add(Calendar.MINUTE, ClusterMapContributeUtils.MINUTE_LOCK);
            if (item.locked_by != null)
                lockString = lockString.replace("_user_", item.locked_by);
            lockString = lockString.replace("_time_", DateTool.getTimeShort(item.locked_at));
            lockString = lockString.replace("_timeFuture_", DateTool.getTimeShort(c.getTime()));

            holder.textViewLocked.setText(lockString);
            holder.textViewLocked.setVisibility(View.VISIBLE);
        }

        // holder.textViewLocked.setText();
        holder.buttonEditLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                listener.onClickEditLayout(v, groupPosition, item);
            }
        });

        holder.buttonEditMetadata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listener != null) {
                    listener.onClickEditMetadata(v, groupPosition, item);
                }
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

    public interface OnEditClickListener {

        void onClickEditLayout(View finalConvertView, int groupPosition, final Master master);

        void onClickEditMetadata(View finalConvertView, int groupPosition, final Master master);
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