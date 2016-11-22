package com.paulvarry.intra42.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.paulvarry.intra42.R;
import com.paulvarry.intra42.api.Announcements;

import java.util.List;


public class ListAdapterAnnouncements extends BaseAdapter {

    private final Context context;
    private List<Announcements> eventsList;

    public ListAdapterAnnouncements(Context context, List<Announcements> projectsList) {

        this.context = context;
        this.eventsList = projectsList;
    }

    /**
     * How many items are in the data set represented by this Adapter.
     *
     * @return Count of items.
     */
    @Override
    public int getCount() {
        return eventsList.size();
    }

    /**
     * Get the data projectsList associated with the specified position in the data set.
     *
     * @param position Position of the projectsList whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public Announcements getItem(int position) {
        return eventsList.get(position);
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
            convertView = vi.inflate(R.layout.list_view_announcements, parent, false);

            holder.textViewTitle = (TextView) convertView.findViewById(R.id.textViewTitle);
            holder.textViewText = (TextView) convertView.findViewById(R.id.textViewText);
            holder.textViewAuthor = (TextView) convertView.findViewById(R.id.textViewAuthor);
            holder.textViewTime = (TextView) convertView.findViewById(R.id.textViewTime);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Announcements item = getItem(position);

        holder.textViewTitle.setText(item.title);
        holder.textViewText.setText(item.text);
        holder.textViewAuthor.setText(item.author);
//        textViewTime.setText(item.createdAt.getRelativeTime());

        return convertView;
    }

    private static class ViewHolder {
        private TextView textViewTitle;
        private TextView textViewText;
        private TextView textViewAuthor;
        private TextView textViewTime;
    }
}