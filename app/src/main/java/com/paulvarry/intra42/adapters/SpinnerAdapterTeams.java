package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Teams;
import com.paulvarry.intra42.utils.ProjectUserStatus;
import com.paulvarry.intra42.utils.Tools;

import java.util.List;

public class SpinnerAdapterTeams extends BaseAdapter {

    private final Context context;
    private List<Teams> teams;

    public SpinnerAdapterTeams(Context context, List<Teams> teams) {

        this.context = context;
        this.teams = teams;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return teams.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Teams getItem(int position) {
        if (teams != null && teams.size() > position)
            return teams.get(position);
        return null;
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        if (teams != null && teams.size() > position)
            return teams.get(position).id;
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (context == null)
            return null;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.spinner_team, parent, false);

            holder.layoutContainer = convertView.findViewById(R.id.layoutContainer);
            holder.textViewNameGroup = convertView.findViewById(R.id.textViewNameGroup);
            holder.textViewMark = convertView.findViewById(R.id.textViewMark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.layoutContainer.setPaddingRelative(
                holder.layoutContainer.getPaddingStart(),
                holder.layoutContainer.getPaddingTop(),
                (int) Tools.dpToPx(context, 0),
                holder.layoutContainer.getPaddingBottom());

        Teams item = getItem(position);

        if (item != null) {
            holder.textViewNameGroup.setText(item.name);
            ProjectUserStatus.setMark(parent.getContext(), item, holder.textViewMark);
            holder.textViewMark.setTextColor(context.getResources().getColor(R.color.textColorSecondary));
            holder.textViewNameGroup.setTextColor(context.getResources().getColor(R.color.textColorSecondary));
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (context == null)
            return null;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater layoutInflater = LayoutInflater.from(context);
            convertView = layoutInflater.inflate(R.layout.spinner_team, parent, false);

            holder.layoutContainer = convertView.findViewById(R.id.layoutContainer);
            holder.textViewNameGroup = convertView.findViewById(R.id.textViewNameGroup);
            holder.textViewMark = convertView.findViewById(R.id.textViewMark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.layoutContainer.setPaddingRelative(
                holder.layoutContainer.getPaddingStart(),
                holder.layoutContainer.getPaddingTop(),
                (int) Tools.dpToPx(context, 16),
                holder.layoutContainer.getPaddingBottom());

        Teams item = getItem(position);

        holder.textViewNameGroup.setText(item.name);
        ProjectUserStatus.setMark(parent.getContext(), item, holder.textViewMark);

        return convertView;
    }

    private static class ViewHolder {
        private ViewGroup layoutContainer;
        private TextView textViewNameGroup;
        private TextView textViewMark;
    }
}
