package com.paulvarry.intra42.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.CursusUsers;

import java.util.List;

public class ListAdapterSkills extends BaseAdapter {

    private final Activity context;
    List<CursusUsers.Skills> skillsList;

    public ListAdapterSkills(Activity context, List<CursusUsers.Skills> skillsList) {

        this.context = context;
        this.skillsList = skillsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return skillsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public CursusUsers.Skills getItem(int position) {
        return skillsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_skills, parent, false);

            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewLevel = (TextView) convertView.findViewById(R.id.textViewLevel);
            holder.progressBarSkills = (ProgressBar) convertView.findViewById(R.id.progressBarSkills);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CursusUsers.Skills item = getItem(position);

        holder.textViewName.setText(item.name);
        holder.textViewLevel.setText(String.valueOf(item.level));
        holder.progressBarSkills.setProgress((int) item.level);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewName;
        private TextView textViewLevel;
        private ProgressBar progressBarSkills;
    }
}