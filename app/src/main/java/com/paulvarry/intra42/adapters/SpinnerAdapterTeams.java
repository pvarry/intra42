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
        return teams.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return teams.get(position).id;
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

            holder.textViewNameGroup = convertView.findViewById(R.id.textViewNameGroup);
            holder.textViewMark = convertView.findViewById(R.id.textViewMark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Teams item = getItem(position);

        holder.textViewNameGroup.setText(item.name);
        ProjectUserStatus.setMark(parent.getContext(), item, holder.textViewMark);
        holder.textViewMark.setTextColor(context.getResources().getColor(R.color.textColorSecondary));
        holder.textViewNameGroup.setTextColor(context.getResources().getColor(R.color.textColorSecondary));

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

            holder.textViewNameGroup = convertView.findViewById(R.id.textViewNameGroup);
            holder.textViewMark = convertView.findViewById(R.id.textViewMark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Teams item = getItem(position);

        holder.textViewNameGroup.setText(item.name);
        ProjectUserStatus.setMark(parent.getContext(), item, holder.textViewMark);
//        holder.textViewNameGroup.setTextColor(context.getResources().getColor(R.color.textColorSecondary));

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewNameGroup;
        private TextView textViewMark;
    }
}
