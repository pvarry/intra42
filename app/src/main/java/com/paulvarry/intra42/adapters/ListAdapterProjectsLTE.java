package com.paulvarry.intra42.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.project.ProjectActivity;
import com.paulvarry.intra42.api.model.ProjectsLTE;

import java.util.List;


public class ListAdapterProjectsLTE extends BaseAdapter {

    private final Activity context;
    private List<ProjectsLTE> projectsLTEList;

    public ListAdapterProjectsLTE(ProjectActivity activity, List<ProjectsLTE> children) {
        this.context = activity;
        this.projectsLTEList = children;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return projectsLTEList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public ProjectsLTE getItem(int position) {
        return projectsLTEList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {

        return projectsLTEList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_projects, parent, false);

            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewSlug = (TextView) convertView.findViewById(R.id.textViewSlug);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            holder.textViewTier = (TextView) convertView.findViewById(R.id.textViewTier);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textViewName.setText(projectsLTEList.get(position).name);
        holder.textViewSlug.setText(projectsLTEList.get(position).slug);

        holder.textViewTier.setVisibility(View.GONE);
        holder.textViewTime.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewName;
        private TextView textViewSlug;
        private TextView textViewTime;
        private TextView textViewTier;
    }
}
