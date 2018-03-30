package com.paulvarry.intra42.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.activities.home.HomeActivity;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.api.model.UsersLTE;
import com.paulvarry.intra42.utils.DateTool;

import java.util.List;

public class ListAdapterCorrections extends BaseAdapter {

    private final HomeActivity activity;
    private List<ScaleTeams> scaleTeamsList;

    public ListAdapterCorrections(HomeActivity activity, List<ScaleTeams> scaleTeamsList) {

        this.activity = activity;
        this.scaleTeamsList = scaleTeamsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return scaleTeamsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public ScaleTeams getItem(int position) {
        return scaleTeamsList.get(position);
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

            LayoutInflater vi = LayoutInflater.from(activity);

            convertView = vi.inflate(R.layout.list_view_corrections, parent, false);
            holder.textViewWith = convertView.findViewById(R.id.textViewWith);
            holder.textViewLogin = convertView.findViewById(R.id.textViewLogin);
            holder.textViewProject = convertView.findViewById(R.id.textViewProject);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
            holder.linearLayoutWith = convertView.findViewById(R.id.linearLayoutWith);
            holder.linearLayoutOn = convertView.findViewById(R.id.linearLayoutOn);
            holder.linearLayoutDate = convertView.findViewById(R.id.linearLayoutDate);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScaleTeams item = getItem(position);

        if (item.corrector != null && item.corrector.isMe(activity.app) && item.correcteds != null) {
            StringBuilder login = new StringBuilder();
            String sep = "";
            for (UsersLTE u : item.correcteds) {
                login.append(sep).append(u.login);
                sep = ", ";
            }
            holder.textViewLogin.setText(login);
        } else if (item.corrector != null && !item.corrector.isMe(activity.app))
            holder.textViewLogin.setText(item.corrector.login);
        else {
            holder.textViewLogin.setText(R.string.evaluation_someone);
        }

        if (item.corrector != null && item.corrector.isMe(activity.app))
            holder.textViewWith.setText(R.string.evaluation_correct_someone);
        else
            holder.textViewWith.setText(R.string.evaluation_corrected_by);

        if (item.scale != null && item.scale.name != null)
            holder.textViewProject.setText(item.scale.name);
        else {
            holder.linearLayoutOn.setVisibility(View.GONE);
        }

        if (item.beginAt == null)
            holder.textViewDate.setVisibility(View.GONE);
        else {
            String str = DateTool.getTodayTomorrow(activity, item.beginAt, true) + DateTool.getDateTimeLong(item.beginAt);
            holder.textViewDate.setVisibility(View.VISIBLE);
            holder.textViewDate.setText(str);
        }

        return convertView;
    }

    static class ViewHolder {

        TextView textViewWith;
        TextView textViewLogin;
        TextView textViewProject;
        TextView textViewDate;
        LinearLayout linearLayoutWith;
        LinearLayout linearLayoutOn;
        LinearLayout linearLayoutDate;

    }
}