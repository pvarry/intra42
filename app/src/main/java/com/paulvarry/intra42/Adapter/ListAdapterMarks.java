package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.Tools.ProjectUserStatus;
import com.paulvarry.intra42.api.ProjectsUsers;

import java.util.List;

public class ListAdapterMarks extends BaseAdapter {

    private final Context context;
    List<ProjectsUsers> projectsList;

    public ListAdapterMarks(Context context, List<ProjectsUsers> projectsList) {

        this.context = context;
        this.projectsList = projectsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return projectsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public ProjectsUsers getItem(int position) {
        return projectsList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return projectsList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(R.layout.list_view_marks, parent, false);

            holder.textViewProjectName = (TextView) convertView.findViewById(R.id.textViewProjectName);
            holder.textViewProjectSlug = (TextView) convertView.findViewById(R.id.textViewProjectSlug);
            holder.textViewProjectMark = (TextView) convertView.findViewById(R.id.textViewProjectMark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProjectsUsers item = getItem(position);

        holder.textViewProjectName.setText(item.project.name);
        holder.textViewProjectSlug.setText(item.project.slug);
        ProjectUserStatus.setMark(context, item, holder.textViewProjectMark);

//        holder.textViewProjectSlug.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewProjectName;
        private TextView textViewProjectSlug;
        private TextView textViewProjectMark;
    }
}