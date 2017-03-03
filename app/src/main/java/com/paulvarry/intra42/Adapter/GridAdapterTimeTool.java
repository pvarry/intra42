package com.paulvarry.intra42.Adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.model.Campus;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class GridAdapterTimeTool extends BaseAdapter {
    Context context;
    private List<Campus> campuses;

    public GridAdapterTimeTool(Context context, List<Campus> campusList) {

        this.context = context;
        this.campuses = campusList;
    }

    @Override
    public int getCount() {
        if (campuses != null)
            return campuses.size();
        return 0;
    }

    @Override
    public Campus getItem(int position) {
        return campuses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return campuses.get(position).id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = vi.inflate(R.layout.grid_view_time_tool, parent, false);
            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);
            holder.textViewDate = (TextView) convertView.findViewById(R.id.textViewDate);
            holder.textViewTimeZone = (TextView) convertView.findViewById(R.id.textViewTimeZone);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Campus campus = getItem(position);

        holder.textViewTitle.setText(campus.name);

        Date date = new Date();
        TimeZone timeZone = TimeZone.getTimeZone(campus.timeZone);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT, Locale.getDefault());
        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.LONG, Locale.getDefault());
        DateFormat dateFormatTimeZone = new SimpleDateFormat("Z", Locale.getDefault());

        timeFormatter.setTimeZone(timeZone);
        dateFormatter.setTimeZone(timeZone);
        dateFormatTimeZone.setTimeZone(timeZone);

        holder.textViewTime.setText(timeFormatter.format(date));
        holder.textViewDate.setText(dateFormatter.format(date));
        if (timeZone != null && campus.timeZone != null && !campus.timeZone.isEmpty()) {
            String str = campus.timeZone + " • " + timeZone.getDisplayName(Locale.getDefault()) + " • " + dateFormatTimeZone.format(date);
            holder.textViewTimeZone.setText(str);
        } else
            holder.textViewTimeZone.setText(R.string.time_zone_error);

        return convertView;
    }

    static class ViewHolder {

        private TextView textViewTitle;
        private TextView textViewTime;
        private TextView textViewDate;
        private TextView textViewTimeZone;

    }
}
