package com.paulvarry.intra42.adapters;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Projects;
import com.paulvarry.intra42.api.model.ProjectsSessions;

import org.ocpsoft.prettytime.PrettyTime;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListAdapterProjects extends BaseAdapter {

    private final Context context;
    List<Projects> projectsList;

    public ListAdapterProjects(Context context, List<Projects> projectsList) {

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
    public Projects getItem(int position) {
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

            convertView = vi.inflate(R.layout.list_view_projects, parent, false);
            holder.textViewName = (TextView) convertView.findViewById(R.id.textViewName);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            holder.textViewTier = (TextView) convertView.findViewById(R.id.textViewTier);
            holder.textViewSlug = (TextView) convertView.findViewById(R.id.textViewSlug);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Projects item = getItem(position);
        ProjectsSessions sessions = ProjectsSessions.getSessionSubscribable(item.sessionsList);
        String time = null;
        if (sessions != null) {
            PrettyTime p = new PrettyTime(Locale.getDefault());
            time = DateUtils.getRelativeTimeSpanString(context, sessions.estimateTime).toString();
            time = p.formatDuration(new Date(System.currentTimeMillis() - sessions.estimateTime * 1000));
        }
        String tier = "T" + String.valueOf(item.tier);

        String str = "";
        if (item.parent != null && item.parent.name != null)
            str += item.parent.name + " / ";
        str += item.name;
        holder.textViewName.setText(str);
        holder.textViewTime.setText(time);
        holder.textViewTier.setText(tier);
        holder.textViewSlug.setText(item.slug);

        return convertView;
    }

    static class ViewHolder {

        private TextView textViewName;
        private TextView textViewTime;
        private TextView textViewTier;
        private TextView textViewSlug;

    }
}