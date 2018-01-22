package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.ProjectsLTE;
import com.paulvarry.intra42.api.model.ProjectsUsers;
import com.paulvarry.intra42.utils.AppSettings;
import com.paulvarry.intra42.utils.ProjectUserStatus;

import java.util.List;

public class ListAdapterMarks extends BaseAdapter {

    private final Context context;
    private List<ProjectsUsers> projectsUsersList;
    private SparseArray<ProjectsLTE> projects;

    public ListAdapterMarks(Context context, List<ProjectsUsers> projectsList) {

        this.context = context;
        this.projectsUsersList = projectsList;

        this.projects = new SparseArray<>();
        for (ProjectsUsers p : projectsUsersList) {
            projects.append(p.project.id, p.project);
        }
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return projectsUsersList.size();
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
        return projectsUsersList.get(position);
    }

    /**
     * Get the row id associated with the specified position in the list.
     *
     * @param position The position of the projectsList within the adapter's data set whose row id we want.
     * @return The id of the projectsList at the specified position.
     */
    @Override
    public long getItemId(int position) {
        return projectsUsersList.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (vi == null)
                return null;
            convertView = vi.inflate(R.layout.list_view_marks, parent, false);

            holder.textViewProjectName = convertView.findViewById(R.id.textViewProjectName);
            holder.textViewProjectSlug = convertView.findViewById(R.id.textViewProjectSlug);
            holder.textViewProjectMark = convertView.findViewById(R.id.textViewProjectMark);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ProjectsUsers item = getItem(position);

        String title = item.project.name;
        if (item.project.parentId != null && projects.get(item.project.parentId) != null)
            title = projects.get(item.project.parentId).name + " - " + title;
        holder.textViewProjectName.setText(title);
        holder.textViewProjectSlug.setText(item.project.slug);
        ProjectUserStatus.setMark(context, item, holder.textViewProjectMark);

        if (AppSettings.Advanced.getAllowAdvancedData(context))
            holder.textViewProjectSlug.setVisibility(View.VISIBLE);
        else
            holder.textViewProjectSlug.setVisibility(View.GONE);

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewProjectName;
        private TextView textViewProjectSlug;
        private TextView textViewProjectMark;
    }
}